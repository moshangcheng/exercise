# -*- coding: utf-8 -*-

from __future__ import with_statement
from fabric.api import *

env.hosts = ['hk-ubuntu.cloudapp.net']
env.user = 'shu'
env.shell = '/usr/bin/zsh -i -c'


def hadoop_version():
	run("hadoop version")

def hello(name="world"):
	print("Hello %s!" % name)

def echo():
	local("echo hello")

def ls():
	run("ls")
