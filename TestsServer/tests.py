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
		self.assertEqual(response.text, 'Could you precise which component of the SonarQube ecosystem your question is about ?')

	def test_gg_info2(self):
		response = send_message(self.gginputs[1])
		self.assertEqual(response.text, '<html><body><h2>500 Internal Server Error</h2></body></html>')

	def test_sd_info1(self):
		response = send_message(self.sdinputs[0])
		self.assertEqual(response.text, 'Your question seems related to SONAR-9384')

	def test_sd_info5(self):
		response = send_message(self.sdinputs[4])
		self.assertEqual(response.text, 'Your question seems related to SONAR-9801')

	def test_sd_info6(self):
		response = send_message(self.sdinputs[5])
		self.assertEqual(response.text, 'Your question seems related to SONAR-10536')

	def test_sd_info7(self):
		response = send_message(self.sdinputs[6])
		self.assertEqual(response.text, 'Your question seems related to SONAR-10502')

	def test_sd_info8(self):
		response = send_message(self.sdinputs[7])
		self.assertEqual(response.text, 'There seems to be no product nor version in your question, could you precise those information ?')

if __name__=='__main__':
	unittest.main()
