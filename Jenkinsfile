pipeline {
    agent any

    environment {
        REGISTRY = 'aws_account_id.dkr.ecr.region.amazonaws.com'
        FLASK_IMAGE = "${REGISTRY}/flask-app"
        DB_IMAGE = "${REGISTRY}/mysql-db"
        HELM_CHART_PATH = 'helm'
        KUBECONFIG = credentials('kubeconfig') // Jenkins credential ID for kubeconfig
        ECR_CREDENTIALS = credentials('ecr-credentials') // Jenkins credential ID for ECR
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Checkout code from GitHub
                git 'https://github.com/your-repo/flask-app.git'
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    // Build Flask app Docker image
                    docker.build("${FLASK_IMAGE}:${env.BUILD_ID}", "-f Dockerfile.flask .")
                    
                    // Build MySQL Docker image (if you have custom configuration)
                    docker.build("${DB_IMAGE}:${env.BUILD_ID}", "-f Dockerfile.mysql .")
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    // Login to AWS ECR
                    withCredentials([usernamePassword(credentialsId: "${ECR_CREDENTIALS}", usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh 'aws ecr get-login-password --region region | docker login --username AWS --password-stdin ${REGISTRY}'
                        
                        // Push Flask app Docker image
                        sh "docker push ${FLASK_IMAGE}:${env.BUILD_ID}"
                        
                        // Push MySQL Docker image
                        sh "docker push ${DB_IMAGE}:${env.BUILD_ID}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Deploy Flask app and MySQL to Kubernetes using Helm
                    withKubeConfig(credentialsId: 'kubeconfig') {
                        sh 'helm upgrade --install flask-app ${HELM_CHART_PATH}/flask-app --set image.repository=${FLASK_IMAGE} --set image.tag=${env.BUILD_ID}'
                        sh 'helm upgrade --install mysql-db ${HELM_CHART_PATH}/mysql-db --set image.repository=${DB_IMAGE} --set image.tag=${env.BUILD_ID}'
                    }
                }
            }
        }
    }

    post {
        always {
            // Clean up resources, if necessary
            cleanWs()
        }
    }
}
