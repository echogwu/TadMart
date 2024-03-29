import os
basedir = os.path.abspath(os.path.dirname(__file__))

SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(basedir, 'tadmart.db')
SQLALCHEMY_MIGRATE_REPO = os.path.join(basedir, 'db_repository')
SQLALCHEMY_TRACK_MODIFICATIONS = True
SECRET_KEY = 'super secret key'
WHOOSH_ENABLED = True
SQLALCHEMY_RECORD_QUERIES = True
WHOOSH_BASE = os.path.join(basedir, 'search.db')
'''
CSRF_ENABLED = True
SECRET_KEY = 'you-will-never-guess'

OPENID_PROVIDERS = [
            {'name': 'Google', 'url': 'https://www.google.com/accounts/o8/id'},
                {'name': 'Yahoo', 'url': 'https://me.yahoo.com'},
                    {'name': 'AOL', 'url': 'http://openid.aol.com/<username>'},
                        {'name': 'Flickr', 'url': 'http://www.flickr.com/<username>'},
                            {'name': 'MyOpenID', 'url': 'https://www.myopenid.com'}]

SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(basedir, 'app.db')
SQLALCHEMY_MIGRATE_REPO = os.path.join(basedir, 'db_repository')

# mail server settings
MAIL_SERVER = 'localhost'
MAIL_PORT = 25
MAIL_USERNAME = None
MAIL_PASSWORD = None

# administrator list
ADMINS = ['you@example.com']
'''
#pagination
ENTRIES_PER_PAGE = 10
MAX_SEARCH_RESULTS = 10
