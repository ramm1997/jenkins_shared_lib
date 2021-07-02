def call(Map pipelineParams){
def projectName = "${pipelineParams.ecrRepoName}"

pipeline {
   agent any
   environment
    {
        VERSION = "${BUILD_NUMBER}"
        PROJECT = "${projectname}"
        IMAGE = "$PROJECT:$VERSION"
        ECRURL = 'https://352318176854.dkr.ecr.ap-south-1.amazonaws.com/${projectname}'
        ECRCRED = 'ecr:ap-south-1:awscredentials'
    }
    stages {
         stage('Image Build'){
             steps{
                 script{
                       docker.build('$IMAGE')
                 }
             }
         }
         stage('Push Image'){
         steps{
             script
                {

                    docker.withRegistry(ECRURL, ECRCRED)
                    {
                        docker.image(IMAGE).push()
                    }
                }
            }
         }
    }

    post
    {
        always
        {
            // make sure that the Docker image is removed
            sh "docker rmi $IMAGE | true"
        }
    }

}
}
