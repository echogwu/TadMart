from src import db
#from sqlalchemy.inspection import inspect
from sqlalchemy import inspect
'''
from flask import Flask
from flask_sqlalchemy import SQLAlchemy


app = Flask(__name__)
app.config.from_object('config')
db = SQLAlchemy(app)
'''

class SerializableModel(db.Model):
 
    __abstract__ = True

    def serialize(self):
        """ Return a dictionary representation of this model.
        """

        ret_data = {}

        columns = self.__table__.columns.keys()
        relationships = self.__mapper__.relationships.keys()

        for key in columns:
            print("column key="+key)
            ret_data[key] = getattr(self, key)

        for key in relationships:
            print("relationship key="+key)
            is_list = self.__mapper__.relationships[key].uselist
            if is_list:
                ret_data[key] = []
                for item in getattr(self, key):
                    #ret_data[key].append(item.serialize())
                    ret_data[key].append(getattr(item,"id"))
            else:
                if self.__mapper__.relationships[key].query_class is not None:
                    #ret_data[key] = getattr(self, key).serialize()
                    ret_data[key] = getattr(getattr(self, key),"id")
                else:
                    ret_data[key] = getattr(getattr(self, key),"id")

        return ret_data

    @staticmethod
    def serialize_list(listOfObjects):
        return [item.serialize() for item in listOfObjects]

class Orderitems(SerializableModel):     # the class name must not be "OrderItems", the table name will be interpreted as "order_items". It doesnot matter if the first letter is capitalized

    id = db.Column(db.Integer, primary_key = True)
    lastUpdateDate = db.Column(db.String)
    asin = db.Column(db.String)
    supplier = db.Column(db.String, nullable=False)
    sellerSKU = db.Column(db.String)
    orderItemId = db.Column(db.String)
    title = db.Column(db.String)
    quantityShipped = db.Column(db.Integer)
    itemPriceCurrency = db.Column(db.String)
    itemPrice = db.Column(db.Float)
    shippingPrice = db.Column(db.Float)
    giftWrapPrice = db.Column(db.Float)
    itemTax = db.Column(db.Float)
    shippingTax = db.Column(db.Float)
    giftWrapTax = db.Column(db.Float)
    shippingDiscount = db.Column(db.Float)
    promotionDiscount = db.Column(db.Float)
    inventory_id = db.Column(db.Integer, db.ForeignKey('inventory.id'))  #"inventory" must be lower case
    '''
    def __repr__(self):
        return '<OrderItem %r>' % (self.title)
    '''

    @property
    def serialization(self):
        result = {}
        for f in inspect(Orderitems).attrs:
            if f.key == 'inventory_id':
                result[f.key] = self.inventoryrecord.id
            result[f.key] = getattr(self, f.key)


class Inventory(SerializableModel):
    id = db.Column(db.Integer, primary_key = True)
    asin = db.Column(db.String)
    supplier = db.Column(db.String)
    sellerSKU = db.Column(db.String, nullable=False)
    fnsku = db.Column(db.String, nullable=False)
    condition = db.Column(db.String)
    totalSupplyQuantity = db.Column(db.Integer)
    inStockSupplyQuantity = db.Column(db.Integer)
    costEach = db.Column(db.Float)
    orders = db.relationship('Orderitems', backref='inventoryRecord', lazy='dynamic')

    '''
    def __repr__(self):
        return '<Inventory %r>' % (self.fnsku)
    '''






