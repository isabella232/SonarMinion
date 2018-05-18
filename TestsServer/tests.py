import requests
import unittest
import os

def send_message(message):
	#url = 'http://localhost:9001/analyze'
	url = 'https://sonar-minion.herokuapp.com/analyze'
	response = requests.post(url, data=message.strip())
	return response


class TestServer(unittest.TestCase):
	def setUp(self):
		self.gginputs = []
		self.sdinputs = []
		self.ggrootdir = os.getcwd()+"/../minion-service/src/test/resources/google_groups/"
		self.sdrootdir = os.getcwd()+"/../minion-service/src/test/resources/servicedesk/"
		for file in os.listdir(self.ggrootdir):
			inputfile = open(self.ggrootdir+file).read()
			self.gginputs.append(inputfile)
		for file in os.listdir(self.sdrootdir):
			if file.startswith('input'):
				inputfile = open(self.sdrootdir+file).read()
				self.sdinputs.append(inputfile)
	

	def test_gg_info1(self):
		response = send_message(self.gginputs[0])
		self.assertEqual(response.text, 'Could you specify which component of the SonarQube ecosystem your question is about ?')

	def test_gg_info2(self):
		response = send_message(self.gginputs[1])
		self.assertEqual(response.text, "We didn't understand the error, could you please describe the error ?")

	def test_sd_info1(self):
		response = send_message(self.sdinputs[0])
		self.assertEqual(response.text, 'JIRA tickets found : SONAR-9384<br/>SONAR-10364<br/>SONAR-10254<br/>SONAR-10398<br/>SONAR-9694<br/>SONAR-10511<br/>SONAR-10567<br/>SONAR-7992<br/>SONAR-9984<br/>SONAR-10593<br/>SONAR-5182<br/>Products found : SonarQube - 6.7.1<br/>Errors found : \tat org.sonar.server.computation.task.projectanalysis.component.VisitException.rethrowOrWrap(VisitException.java:44)<br/>\tat org.sonar.server.computation.task.projectanalysis.measure.MeasureRepositoryImpl.add(MeasureRepositoryImpl.java:124)')

	def test_sd_info5(self):
		response = send_message(self.sdinputs[4])
		self.assertEqual(response.text, "We didn't understand the error, could you please describe the error ?")

	def test_sd_info6(self):
		response = send_message(self.sdinputs[5])
		self.assertEqual(response.text, "We didn't understand the error, could you please describe the error ?")

	def test_sd_info7(self):
		response = send_message(self.sdinputs[6])
		self.assertEqual(response.text, "We didn't understand the error, could you please describe the error ?")

	def test_sd_info8(self):
		response = send_message(self.sdinputs[7])
		self.assertEqual(response.text, 'Seems like there is no product nor version in your question, could you clarify this information ?')

if __name__=='__main__':
	unittest.main()
