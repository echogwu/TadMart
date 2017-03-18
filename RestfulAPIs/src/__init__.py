from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from config import basedir

app = Flask(__name__)
app.config.from_object('config')
app.secret_key = "super secret key"
db = SQLAlchemy(app)

from src import views, models
