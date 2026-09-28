def dockerImage

pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        DOCKERHUB_REPO = 'anzuniks/temperature-converter'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script { runCmd('mvn -B clean verify') }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    recordCoverage(tools: [[parser: 'JACOCO', pattern: '**/target/site/jacoco/jacoco.xml']])
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    dockerImage = docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                        dockerImage.push()
                        dockerImage.push("${env.BUILD_NUMBER}")
                    }
                }
            }
        }
    }
}

def runCmd(String cmd) {
    if (isUnix()) {
        sh cmd
    } else {
        bat cmd
    }
}
