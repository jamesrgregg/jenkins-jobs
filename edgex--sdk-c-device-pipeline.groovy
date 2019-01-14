// this script uses docker to build the sdk instead of maven:3.6.0-jdk-8-alpine
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
                sh 'pwd'

            }
        }
        
        stage('Clone Repository') {
            container('git') {
                sh 'git clone -b master https://github.com/edgexfoundry/device-sdk-c.git'
            }
        }

        stage('Build SDK') {
            container('docker') {
                dir('device-sdk-c/') {
                    sh 'docker build -f scripts/Dockerfile.alpine-3.7 .'
                    sh 'ls -hal ./edgex-c-sdk/build/release/'
                }
            }
        }
        stage('Archive Output'){
         
         // Archive the build artifacts
         
         archiveArtifacts artifacts: 'device-sdk-c/edgex-c-sdk/build/release/*.gz' , onlyIfSuccessful: true
        }
    }
}