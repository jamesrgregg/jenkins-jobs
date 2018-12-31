podTemplate(label: 'mypod', containers: [
    containerTemplate(name: 'git', image: 'alpine/git', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true)
  ],
  volumes: [
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
  ]
  ) {
    node('mypod') {
        stage('Check running containers') {
            container('docker') {
                // example to show you can run docker commands when you mount the socket
                sh 'hostname'
                sh 'hostname -i'
                sh 'docker ps'
                sh 'docker images'
            }
        }
        
        stage('Clone repository') {
            container('git') {
                sh 'whoami'
                sh 'hostname -i'
                sh 'git clone -b master https://github.com/edgexfoundry/device-grove-c.git'
            }
        }

        stage('Build device-grove-c') {
            container('docker') {
                dir('device-grove-c/') {
                    sh 'hostname'
                    sh 'hostname -i'
                    sh 'docker build . -t device-grove-c -f ./scripts/Dockerfile.alpine-3.8'
                }
            }
        }
    }
}
