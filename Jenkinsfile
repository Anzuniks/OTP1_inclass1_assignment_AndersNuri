// Needs Jenkins plugins: Pipeline, Git, JUnit, JaCoCo, Docker Pipeline
// and a "Username with password" credential for Docker Hub with the id below.
def dockerImage

pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'Docker_Hub'
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
                // Tests use an in-memory H2 database, so no MariaDB is needed here
                script { runCmd('mvn -B clean verify') }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/Launcher.class'
                    )
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

// Works on both Linux (sh) and Windows (bat) Jenkins agents
def runCmd(String cmd) {
    if (isUnix()) {
        sh cmd
    } else {
        bat cmd
    }
}
