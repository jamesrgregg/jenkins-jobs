podTemplate(label: 'mypod', containers: [
  containerTemplate(name: 'git', image: 'alpine/git', ttyEnabled: true, command: 'cat'),
  containerTemplate(name: 'docker', image: 'docker', ttyEnabled: true, command: 'cat'),
  containerTemplate(name: 'docker-awscli', image: 'jrgreggdevops/docker-awscli:1.0', ttyEnabled: true, command: 'cat')
 ],
 volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
 ]
) {
 node('mypod') {
  stage('Pull Docker Image') {
   container('docker') {
    // example to show you can run docker commands when you mount the socket
    sh 'docker -v'
    //sh 'docker ps'
    //sh 'pwd'
    sh 'docker pull jrgreggdevops/docker-awscli:1.0'
   }
  }

  stage('Run Docker AWS CLI') {
   // Query AWS API
   container('docker') {
    docker.image('jrgreggdevops/docker-awscli:1.0').inside {

     withCredentials([usernamePassword(

      credentialsId: 'aws-development-creds',

      passwordVariable: 'AWS_SECRET_ACCESS_KEY',

      usernameVariable: 'AWS_ACCESS_KEY_ID')]) {

      sh 'aws ec2 describe-instances --region us-west-2 --filter Name=tag:Owner,Values=ownername --query Reservations[*].Instances[*].[InstanceId,ImageId,LaunchTime,Tags[`Owner`].Value] > auditResults.txt'
     }

    }

    stage "Archive Audit Output"

     // Archive the build output artifacts.

    archiveArtifacts artifacts: 'auditResults.txt', onlyIfSuccessful: true

   }
  }
 }
}
