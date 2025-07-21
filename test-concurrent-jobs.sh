#!/bin/bash

# Your API endpoint
URL="http://localhost:8080/api/jobs/submit-multiple"

# Create a JSON array of 5 job objects
DATA='['
for i in {1..5}; do
  DATA+='{"jobName":"Job-'$i'","parameters":"key=value'$i'"}'
  if [ $i -ne 5 ]; then
    DATA+=','
  fi
done
DATA+=']'

# Send the request
curl -s -X POST $URL \
  -H "Content-Type: application/json" \
  -d "$DATA" | sed 's/},{/}\n{/g'

echo -e "\nAll concurrent job requests sent."
