#!/bin/bash

if [ "$1" == "dev" ] || [ "$1" == "development" ]; then
    echo "Switching to development environment..."
    cp .env.development .env
    echo "Done! Development environment activated."
elif [ "$1" == "prod" ] || [ "$1" == "production" ]; then
    echo "Switching to production environment..."
    cp .env.production .env
    echo "Done! Production environment activated."
else
    echo "Usage: ./switch-env.sh [dev|prod]"
    echo "  dev, development - Switch to development environment"
    echo "  prod, production - Switch to production environment"
    exit 1
fi


echo "Current environment variables:"
grep -v "^#" .env | grep -v "^$"