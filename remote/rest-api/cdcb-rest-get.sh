#!/bin/sh

source ../.env

cmd="curl --user '${CBCD_USER_ID}:${CBCD_USER_PASS}' -XGET '${CBCD_URL}/rest/v1.0/$@'"
echo "Command:"
echo "${cmd}"
echo "Response:"
eval $cmd
