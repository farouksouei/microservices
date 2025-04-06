 pipeline {
   agent any
   environment {
     dockerCredentials = credentials('dockerhub')
     configImageName = "configuration-service"
     registryCredentials = "nexusCreds"
     registry = "127.0.0.1:8087/"
   }
   stages {
     stage('gitlab') {
       steps {
         git branch: 'dev', credentialsId: 'gitlab-creds', url: 'https://gitlab.com/AlaaBenFradj/kripton-talent.git'

       }
     }
     stage('Setting-up') {
       steps {
        bat 'docker-compose up -d keycloak broker sonarqube nexus '
        bat 'echo "preparing ..."'
       }
     }
     stage('Cleaning') {
       steps {
         script {
           def modules = ['configuration-service', 'candidate-service', 'job-service', 'qualification-service', 'communication-service', 'user-service', 'api-gateway-service', 'registry-service']
           for (module in modules) {
             dir(module) {
              bat '''
                    ./mvnw clean
                '''
             bat 'echo "cleaning ..."'
             }
           }
         }
       }
     }
     stage('Config-server') {
      steps {
          bat 'echo "preparing ..."'
         powershell '''
            cd configuration-service
            ./mvnw package
            docker build -t configuration-service .
          '''
        bat '''
            docker run -d -p 8090:8090 --name configuration-service configuration-service
         '''

      }
     }
     stage('Testing') {
      steps {
          bat 'echo "test ..."'
         script {
          def modules = ['candidate-service', 'job-service', 'qualification-service', 'communication-service', 'user-service', 'api-gateway-service', 'registry-service']
          for (module in modules) {
             dir(module) {
              bat '''
                ./mvnw test
                '''
             }
          }
         }
         bat 'docker stop configuration-service'
         bat 'docker rm configuration-service'

      }
     }
     stage('Package') {
      steps {
          bat 'echo "package ..."'
         script {
          def modules = ['candidate-service', 'job-service', 'qualification-service', 'communication-service', 'user-service', 'api-gateway-service', 'registry-service']
          for (module in modules) {
             dir(module) {
              bat '''
              ./mvnw package -Dmaven.test.skip
              '''
             }
          }
         }
      }
     }
     stage('SonarQube') {
      steps {
          bat 'echo "preparing ..."'
         withSonarQubeEnv(credentialsId: 'sonarCreds', installationName: 'sonar') {
          bat 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar'
         }
      }
     }
     stage('Deployment') {
      steps {
         script {
             bat 'echo "preparing ..."'
          def modules = ['candidate-service', 'job-service', 'qualification-service', 'communication-service', 'user-service', 'api-gateway-service', 'registry-service','configuration-service']
          for (module in modules) {
             dir(module) {
              moduleImage = docker.build module
              docker.withRegistry('http://' + registry, registryCredentials) {
                  moduleImage.push('latest')
              }
             }
          }
         }
      }
     }
     stage('Running') {
      steps {
         powershell 'docker-compose up -d kripton-talent-front'
      }
     }
     stage('Cleaning-up') {
      steps {
         powershell 'docker system prune -f'
      }
     }

     stage('notify') {
       steps {
         echo 'Notify GitLab'
         updateGitlabCommitStatus name: 'build', state: 'pending'
         updateGitlabCommitStatus name: 'build', state: 'success'
       }
     }
   }
 }