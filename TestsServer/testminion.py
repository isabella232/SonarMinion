import requests
import unittest
from selenium import webdriver
import os

class TestMinion(unittest.TestCase):
	def setUp(self):
		self.br = webdriver.Firefox()
		self.url = 'https://sonar-minion.herokuapp.com/'
		self.component = 'SonarQube'
		self.component_version = 'v7.1'
		self.message = open('message.txt').read()
		self.message2 = open('message-2.txt').read()
		self.precisemessage = '/api/organizations/update_project_visibility'

	def test_minion_broad(self):
		self.br.get(self.url)
		self.br.find_element_by_id('component').clear()
		self.br.find_element_by_id('component').send_keys(self.component)
		self.br.find_element_by_id('component_version').clear()
		self.br.find_element_by_id('component_version').send_keys(self.component_version)
		self.br.find_element_by_id('description').clear()
		self.br.find_element_by_id('description').send_keys(self.message)
		self.br.find_elements_by_tag_name('button')[0].click()
		self.br.implicitly_wait(2)
		response = self.br.find_elements_by_tag_name('body')[0].text
		self.assertEqual(response, "We didn't understand the error, could you please describe the error ?")


	def test_minion_precise_message(self):
		self.br.get(self.url)
		self.br.find_element_by_id('component').clear()
		self.br.find_element_by_id('component').send_keys(self.component)
		self.br.find_element_by_id('component_version').clear()
		self.br.find_element_by_id('component_version').send_keys(self.component_version)
		self.br.find_element_by_id('description').clear()
		self.br.find_element_by_id('message').clear()
		self.br.find_element_by_id('message').send_keys(self.precisemessage)
		self.br.find_elements_by_tag_name('button')[0].click()
		self.br.implicitly_wait(2)
		response = self.br.find_elements_by_tag_name('body')[0].text
		pos = response.find('JIRA tickets found')
		self.assertTrue(pos > -1)

	def test_minion_message_2(self):
		self.br.get(self.url)
		self.br.find_element_by_id('component').clear()
		self.br.find_element_by_id('component').send_keys(self.component)
		self.br.find_element_by_id('component_version').clear()
		self.br.find_element_by_id('component_version').send_keys(self.component_version)
		self.br.find_element_by_id('description').clear()
		self.br.find_element_by_id('description').send_keys(self.message2)
		self.br.find_elements_by_tag_name('button')[0].click()
		self.br.implicitly_wait(2)
		response = self.br.find_elements_by_tag_name('body')[0].text
		pos = response.find('JIRA tickets found')
		self.assertTrue(pos > -1)

	def tearDown(self):
		self.br.close()

if __name__=='__main__':
	unittest.main()
