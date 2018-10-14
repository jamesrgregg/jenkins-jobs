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
                sh 'docker -v'
                sh 'docker run --name detached-nginx -p 8066:8066 -d nginx'
                sh 'docker ps'
            }
        }
        
        stage('Clone repository') {
            container('git') {
                sh 'git clone -b master https://github.com/docker/docker-bench-security.git'
		        // sh 'cd docker-bench-security/'
            }
        }
        stage('Build & Run Docker Security Bench') {
            container('docker') {
                dir('docker-bench-security/') {
                sh 'docker build -t docker-bench-security .'
                sh 'docker run --net host --pid host --cap-add audit_control -e DOCKER_CONTENT_TRUST=$DOCKER_CONTENT_TRUST -v /var/lib:/var/lib -v /var/run/docker.sock:/var/run/docker.sock -v /usr/lib/systemd:/usr/lib/systemd -v /etc:/etc --label docker_bench_security docker-bench-security -c check_1,check_1_1,check_2,check_2_1,check_2_8,check_2_11,check_2_12,check_2_14,check_2_15,check_2_18,check_4,check_4_1,check_5,check_5_2,check_5_26,check_5_28,check_7,check_7_1 -x k8s'
                sh 'docker rmi docker-bench-security --force'
                sh 'docker stop detached-nginx'
                sh 'docker rm detached-nginx'
                
                }
	        }
        }
    }
}