`pipeline {
    agent any
    
    stages {

        stage{'Get code from github'} {

            steps {
                git branch 'main',
                credentialsId: '0fb76644-59be-470a-a3dc-b73810d0c458',
                url: 'https://github.com/moustafa-ismail/sprints-project'
                sh 'cd Sprints_W5-_Final'
                sh 'ls -altr'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t mohsen0808/project:latest .'
            }
        }
        stage('Push Image to Nexus') {
            steps {
                script {
                    // Replace with your Nexus credentials
                    nexusUser = 'admin'
                    nexusPassword = '123'

                    // Replace with your Nexus repository URL and image name
                    nexusUrl = 'http://ad88db502381e46a4b47f813036bb426-421067397.us-west-1.elb.amazonaws.com:8081/'
                    imageName = 'mohsen0808/project:latest'

                    //sh "docker login -u ${nexusUser} -p ${nexusPassword} ${nexusUrl}"
                    sh "docker push ${nexusUrl}/${imageName}"
                    //sh "docker logout ${nexusUrl}"
                }
            }
        }
    }
}
