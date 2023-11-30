if [ -z "$1" ]
  then
    echo "Running workflow without event file"
    act -W ./.github/workflows/publish-container.yml --var AWS_ENDPOINT="http://localhost:4566" --var REPOSITORY_ENDPOINT="localhost.localstack.cloud:4510" -s ECR_PASSWORD="000000000000-auth-token" --var AWS_REGION="eu-central-1" -s AWS_ACCESS_KEY_ID=test -s AWS_SECRET_ACCESS_KEY=test
else
  echo "Running workflow with event file: $1"
  act -W ./.github/workflows/publish-container.yml --eventpath $1 --var AWS_ENDPOINT="http://localhost:4566" --var REPOSITORY_ENDPOINT="localhost.localstack.cloud:4510" -s ECR_PASSWORD="000000000000-auth-token" --var AWS_REGION="eu-central-1" -s AWS_ACCESS_KEY_ID=test -s AWS_SECRET_ACCESS_KEY=test
fi
