try:
    from setuptools import setup
except ImportError:
    from distutils.core import setup

setup(
    name='ak-lambda-analysis',
    version='1.0-SNAPSHOT',
    packages=['tests', 'analysis'],
    url='https://github.com/eigengo/ak-lambda',
    license='',
    author='Tom Bocklisch',
    author_email='tomb@cakesolutions.net',
    description='Spark analysis for the ak-lambda project',
    requires=['numpy', 'neon']
)
