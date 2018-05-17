SonarMinion
===========
[![Build Status](https://travis-ci.org/SonarSource/SonarMinion.svg?branch=master)](https://travis-ci.org/SonarSource/SonarMinion)


`curl -d '{"description":foo, "component":"SonarQube", "component_version":"v1"}' "https://sonar-minion.herokuapp.com/analyze"`


Execution des tests de reponse du serveur:

requirements:
- python 2.7

> pip install requests

> pip install unittest

depuis le repertoire TestsServer:
> python -m tests -vvv
