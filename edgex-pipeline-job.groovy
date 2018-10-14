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
                sh 'docker -v'
                sh 'docker images'
            }
        }
        
        stage('Clone repository') {
            container('git') {
                sh 'wget https://raw.githubusercontent.com/edgexfoundry/developer-scripts/master/compose-files/docker-compose.yml'
		        sh 'ls -hal'
		        sh 'pwd'
            }
        }
        stage('Pull all EdgeX images') {
            container('compose') {
                dir('.') {
                // step([$class: 'DockerComposeBuilder', dockerComposeFile: 'docker-compose.yml', option: [$class: 'ExecuteCommandInsideContainer', command: 'pull', index: 1, privilegedMode: false, service: 'volume', workDir: '.'], useCustomDockerComposeFile: true])
                sh 'docker-compose pull'
            }
        stage('Start EdgeX Services') {
            container('compose') {
                dir('.') {
                // step([$class: 'DockerComposeBuilder', dockerComposeFile: 'docker-compose.yml', option: [$class: 'ExecuteCommandInsideContainer', command: 'pull', index: 1, privilegedMode: false, service: 'volume', workDir: '.'], useCustomDockerComposeFile: true])
                sh 'docker-compose up -d volume && sleep 5'
                sh 'docker-compose up -d config-seed && sleep 60'
                sh 'docker-compose up -d mongo && sleep 10'
                sh 'docker-compose up -d logging && sleep 60'
                sh 'docker-compose up -d notifications && sleep 30'
                sh 'docker-compose up -d metadata && sleep 60'
                sh 'docker-compose up -d data && sleep 60'
                sh 'docker-compose up -d command && sleep 60'
                sh 'docker-compose up -d scheduler && sleep 60'
                sh 'docker-compose up -d export-client && sleep 60'
                sh 'docker-compose up -d export-distro && sleep 60'
                sh 'docker-compose up -d rulesengine && sleep 60'
                sh 'docker-compose up -d device-virtual && sleep 60'
            }        
	        }
        }
    }
}
}
  }