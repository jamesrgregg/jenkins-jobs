podTemplate(label: 'mypod', containers: [
    containerTemplate(name: 'git', image: 'alpine/git', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'maven', image: 'maven:3.6.0-jdk-8-alpine', command: 'cat', ttyEnabled: true),
    containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true)
  ],
  volumes: [
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
  ]
  ) {
    node('mypod') {
        stage('Start Agent') {
            container('docker') {
                // example to show you can run docker commands when you mount the socket
                sh 'hostname'
            }
        }
        
        stage('Clone Repository') {
            container('git') {
                sh 'git clone -b master https://github.com/edgexfoundry/device-sdk-c.git'
            }
        }

        stage('Build SDK') {
            container('maven') {
                dir('device-sdk-c/') {
                    sh 'apk add --update --no-cache build-base wget git gcc cmake make yaml-dev libcurl curl-dev libmicrohttpd-dev'
                    sh './scripts/build.sh'
                    // sh 'ls -hal ./build/release/'
                    sh 'pwd'
                    sh 'ls -hal'
                    sh 'mkdir allure-results'
                }
            }
        }

        stage('Generate Reports') {
            container('maven') {
                dir('device-sdk-c/') {
                    allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: 'target/allure-results']]
                    ])
            }
            }
        }        
        stage('Archive Output'){
         
         // Archive the build artifacts
         
         archiveArtifacts artifacts: 'device-sdk-c/build/release/*.gz' , onlyIfSuccessful: true
        }
    }
}