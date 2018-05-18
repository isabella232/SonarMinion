SonarMinion
===========
[![Build Status](https://travis-ci.org/SonarSource/SonarMinion.svg?branch=master)](https://travis-ci.org/SonarSource/SonarMinion)


`curl -d '{"description":foo, "component":"SonarQube", "component_version":"v1"}' "https://sonar-minion.herokuapp.com/analyze"`


Tests of the server responses:

requirements:
- python 2.7

> pip install requests

> pip install unittest

> pip install selenium

Download geckodriver binary from https://github.com/mozilla/geckodriver/releases and put it somewhere in the PATH

depuis le repertoire TestsServer:
> python -m testminion -vvv
