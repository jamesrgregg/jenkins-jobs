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
    sh 'docker images'
    sh 'docker images -f dangling=true'
    sh 'docker ps -a -f status=exited'
    //sh 'docker rm detached-nginx'
    //sh 'docker rm docker-nonroot-nginx'

   }
  }
  stage('Starting Cleanup ') {
   container('compose') {
    dir('.') {
     // step([$class: 'DockerComposeBuilder', dockerComposeFile: 'docker-compose.yml', option: [$class: 'ExecuteCommandInsideContainer', command: 'pull', index: 1, privilegedMode: false, service: 'volume', workDir: '.'], useCustomDockerComposeFile: true])
     sh 'echo starting cleanup of all Docker images'
     //sh 'docker-compose stop'
     //sh 'docker-compose down'
     sh 'docker stop nginx'
     sh 'docker rm nginx'
    }
    stage('Cleanup images') {
     container('docker') {
      dir('.') {
       //sh 'docker rmi docker-nonroot-nginx --force'
       sh 'docker images'
       sh 'docker system df'
      }
     }
    }
   }
  }
 }
}
