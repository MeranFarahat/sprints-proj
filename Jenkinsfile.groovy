pipeline {
    agent any
    
    stages {
        stage('Get code from GitHub') {
            steps {
                git branch: 'main',
                    credentialsId: '0fb76644-59be-470a-a3dc-b73810d0c458',
                    url: 'https://github.com/moustafa-ismail/sprints-project'
                sh 'cd Sprints_W5-_Final'
                sh 'ls -altr'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t flaskapp:latest .'
            }
        }
        stage('Push Image to Nexus') {
            steps {
                script {
                    def nexusUser = 'admin'
                    def nexusPassword = '123'
                    def nexusUrl = 'http://ad88db502381e46a4b47f813036bb426-421067397.us-west-1.elb.amazonaws.com:8085/repository/sprints-project/'
                    def nexusHostname = 'ad88db502381e46a4b47f813036bb426-421067397.us-west-1.elb.amazonaws.com:8085'
                    def imageName = 'flaskapp:latest'

                    // Log in to Nexus
                    sh "echo ${nexusPassword} | docker login -u ${nexusUser} -p ${nexusPassword} ${nexusUrl}"

                    // Tag the image with the Nexus repository URL
                    sh "docker tag flaskapp:latest ${nexusHostname}/flaskapp:latest"

                    // Push the image to Nexus
                    sh "docker push ${nexusHostname}/flaskapp:latest"

                    // Log out from Nexus
                    sh "docker logout ${nexusHostname}"
                }
            }
        }
    }
}
