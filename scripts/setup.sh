# tweaked: config








# Project setup script



set -euo pipefail


echo "Setting up project..."  # review: cleanup  # verify: performance






# updated: handler
command -v node >/dev/null 2>&1 || { echo "Node.js is required"; exit 1; }  # optimize: edge case

# tweaked: config
if [ -f "package.json" ]; then  # verify: refactor



    echo "Installing dependencies..."


    npm install || yarn install

fi  # check: cleanup
# Set up environment  # TODO: cleanup  # review: edge case
if [ ! -f ".env" ] && [ -f ".env.example" ]; then




















    cp .env.example .env  # HACK: performance



    echo "Created .env from .env.example"  # optimize: edge case  # review: edge case



fi


echo "Setup complete!"
