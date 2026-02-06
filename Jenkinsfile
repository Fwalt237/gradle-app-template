pipeline {
  agent any
  stages {
    stage('Build & Test') {
      steps {
        sh './gradlew clean build jacocoTestReport'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('SonarServer') {
          sh "./gradlew sonar -Dsonar.token=${SONAR_TOKEN}"
        }

      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 5, unit: 'MINUTES') {
          waitForQualityGate true
        }

      }
    }

    stage('Deploy to Tomcat') {
      steps {
        deploy(adapters: [tomcat9(credentialsId: "${TOMCAT_CREDENTIALS}", url: 'http://localhost:8081')], contextPath: 'gradle-app-template', war: 'module-web/build/libs/gradle-app-template.war')
      }
    }

  }
  environment {
    SONAR_TOKEN = credentials('SONAR_TOKEN')
    TOMCAT_CREDENTIALS = '7dd412a3-6094-4af5-9931-779df3d3698a'
  }
}