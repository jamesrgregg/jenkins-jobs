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
    sh 'docker images -a |grep "edgex"'
    sh 'docker images -f dangling=true'
    sh 'docker system df'
    sh 'docker ps -a -f status=exited'

   }
  }

  stage('Check Disk for Pruning') {
   container('git') {
    sh 'wget https://raw.githubusercontent.com/edgexfoundry/developer-scripts/master/compose-files/docker-compose.yml'
    sh 'ls -hal'
    sh 'pwd'
   }
  }
  stage('Starting Cleanup ') {
   container('compose') {
    dir('.') {
     // step([$class: 'DockerComposeBuilder', dockerComposeFile: 'docker-compose.yml', option: [$class: 'ExecuteCommandInsideContainer', command: 'pull', index: 1, privilegedMode: false, service: 'volume', workDir: '.'], useCustomDockerComposeFile: true])
     sh 'echo starting cleanup of all EdgeX images'
     sh 'docker-compose stop'
     //sh 'docker-compose down -v --rmi all --remove-orphans'
     sh 'docker-compose down'
    }
    stage('Cleanup images') {
     container('docker') {
      dir('.') {
       // sh 'docker rmi edgexfoundry/docker-core-config-seed-go:0.6.1 --force'
       // sh 'docker rmi edgexfoundry/docker-support-notifications-go:0.6.1 --force'
       // sh 'docker rmi edgexfoundry/docker-support-logging-go:0.6.1 --force'
       // sh 'docker rmi edgexfoundry/docker-core-command-go:0.6.1 --force'
       // sh 'docker rmi edgexfoundry/docker-core-metadata-go:0.6.1 --force'
       // sh 'docker rmi edgexfoundry/docker-core-data-go:0.6.1 --force'
       // sh 'docker rmi edgexfoundry/docker-export-distro-go:0.6.1 --force'
       // sh 'docker rmi edgexfoundry/docker-export-client-go:0.6.1 --force'
       // sh 'docker rmi edgexfoundry/docker-support-rulesengine:0.6.0 --force'
       // sh 'docker rmi edgexfoundry/docker-device-virtual:0.6.0 --force'
       // sh 'docker rmi edgexfoundry/docker-support-scheduler:0.6.0 --force'
       // sh 'docker rmi edgexfoundry/docker-edgex-mongo:0.6.0 --force'
       // sh 'docker rmi edgexfoundry/docker-edgex-volume:latest --force'
       // sh 'docker rmi jrgreggdevops/docker-nonroot-nginx --force'
       sh 'docker images'
       sh 'docker system df'
      }
     }
    }
   }
  }
 }
}