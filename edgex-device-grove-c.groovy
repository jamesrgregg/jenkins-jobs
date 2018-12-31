podTemplate(label: 'mypod', containers: [
    containerTemplate(name: 'git', image: 'alpine/git', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true)
  ],
  volumes: [
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
  ]
  ) {
    node('mypod') {
        stage('Start Agent and Cleanup') {
            container('docker') {
                // example to show you can run docker commands when you mount the socket
                sh 'docker images'
                // sh 'echo starting cleanup of previous edgex device-grove-c build'
                // sh 'docker rm $(docker ps -a -f status=exited) || docker rmi $(docker images -q -f dangling=true)'
                // sh 'docker rmi device-grove-c'
            }
        }
        
        stage('Clone Repository') {
            container('git') {
                sh 'echo clone edgex device-grove-c repo'
                sh 'git clone -b master https://github.com/edgexfoundry/device-grove-c.git'
            }
        }

        stage('Build device-grove-c') {
            container('docker') {
                dir('device-grove-c/') {
                sh 'docker build . -t device-grove-c -f ./scripts/Dockerfile.alpine-3.8'
                }
            }
        }
    }
}
