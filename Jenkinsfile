pipeline {
  agent any

  environment {
    SONAR_TOKEN = credentials('SONAR_TOKEN')
    JAR_PATH = 'module-web/build/libs/gradle-app-template.jar'
  }

  stages {
    stage('Build & Test') {
      steps {
        sh './gradlew clean :module-web:bootJar jacocoTestReport'
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

    stage('Archive Artifact') {
      steps {
        archiveArtifacts artifacts: "${JAR_PATH}", fingerprint: true
      }
    }

    stage('Local Deployment') {
      steps {
        script {
          sh "pkill -f 'gradle-app-template.jar' || true"
          sh "nohup java -jar ${JAR_PATH} > app.log 2>&1 &"
        }
      }
    }

  }
}