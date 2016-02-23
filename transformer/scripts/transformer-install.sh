#!/bin/bash
scp ../../transformer/target/transformer-1.0-SNAPSHOT.jar root@atlas.networkedassets.net:/opt/autodoc
ssh root@atlas.networkedassets.net /opt/autodoc/transformer-restart.sh