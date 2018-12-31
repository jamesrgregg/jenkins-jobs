podTemplate(label: 'mypod', containers: [
  containerTemplate(name: 'git', image: 'alpine/git', ttyEnabled: true, command: 'cat'),
  containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'compose', image: 'docker/compose:1.17.1', command: 'cat', ttyEnabled: true)

 ],
 volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
 ]
) {
 node('mypod') {
  stage('Check running containers') {
   container('docker') {
    // example to show you can run docker commands when you mount the socket
    sh 'echo list all of the images inside Jenkins'
    sh 'docker images'
    sh 'docker images -f dangling=true'
    sh 'echo list all of the containers inside Jenkins'
    sh 'docker ps'
    sh 'docker ps -a -f status=exited'
   }
  }
  stage('Starting Cleanup ') {
   container('docker') {
    dir('.') {
     // change container to compose image if cleaning up via docker-compose
     // step([$class: 'DockerComposeBuilder', dockerComposeFile: 'docker-compose.yml', option: [$class: 'ExecuteCommandInsideContainer', command: 'pull', index: 1, privilegedMode: false, service: 'volume', workDir: '.'], useCustomDockerComposeFile: true])
     // sh 'docker-compose stop'
     // sh 'docker-compose down'
     sh 'echo starting cleanup of all Docker images'
     sh 'echo disk space check before cleanup'
     sh 'docker system df'
     sh 'docker rm $(docker ps -a -f status=exited) || docker rmi $(docker images -q -f dangling=true)'
    }
    stage('Cleanup Images') {
     container('docker') {
      dir('.') {
       //sh 'docker rmi docker-nonroot-nginx --force'
       sh 'docker rmi device-grove-c'
       sh 'docker images'
       sh 'echo disk space check after cleanup'
       sh 'docker system df'
      }
     }
    }
   }
  }
 }
}