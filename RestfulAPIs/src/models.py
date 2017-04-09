from src import db, app
#from sqlalchemy.inspection import inspect
from sqlalchemy import inspect
from config import WHOOSH_ENABLED
import flask_whooshalchemy as wa

# need to check if whooshalchemy supports python 3

import sys
if sys.version_info >= (3, 0):
    enable_search = False
else:
    enable_search = WHOOSH_ENABLED
    if enable_search:
        import flask_whooshalchemy as wa



class SerializableModel(db.Model):

    __abstract__ = True

    def serialize(self):
        """ Return a dictionary representation of this model.
        """

        ret_data = {}

        columns = self.__table__.columns.keys()
        relationships = self.__mapper__.relationships.keys()

        for key in columns:
            #print("column key="+key)
            ret_data[key] = getattr(self, key)

        #the table Inventory has 3 relationships. the keys are: orders, categoryRecord, supplierRecord. 
        for key in relationships:
            #print("key=%s" % key)
            #print("relationship key="+key)
            is_list = self.__mapper__.relationships[key].uselist
            if is_list:
                #print("is_list=%s" % is_list)         #the relationship "orders" excute this block
                ret_data[key] = []
                for item in getattr(self, key):
                    #ret_data[key].append(item.serialize())
                    ret_data[key].append(getattr(item,"id"))
            else:
                if self.__mapper__.relationships[key].query_class is not None:    #not quite sure under what condition will .query_class return not None.
                    print(self.__mapper__.relationships[key].query_class)
                    #ret_data[key] = getattr(self, key).serialize()
                    ret_data[key] = getattr(getattr(self, key),"id")
                else:
                    item = getattr(self, key)                                   #the two relationships "categoryRecord" and "supplieRecord" will execute this block". Since we don't have any entries in Suppliers and Categories. We need to add this logical operation here.
                    ret_data[key] = getattr(item,"id") if item else ''

        return ret_data

    @staticmethod
    def serialize_list(listOfObjects):
        return [item.serialize() for item in listOfObjects]

class Orderitems(SerializableModel):     # the class name must not be "OrderItems", the table name will be interpreted as "order_items". It doesnot matter if the first letter is capitalized
    __searchable__ = ['asin', 'supplier', 'sellerSKU', 'orderItemId', 'title', 'itemPrice', 'shippingPrice', 'category', 'profit']
    #__searchable__ = ['asin', 'supplier', 'sellerSKU', 'orderItemId', 'title', 'itemPrice', 'shippingPrice', 'category']

    id = db.Column(db.Integer, primary_key = True)
    lastUpdateDate = db.Column(db.String)
    asin = db.Column(db.String)
    supplier = db.Column(db.String)    #copy from the inventory record, can not be edited
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
    category = db.Column(db.String)      #copy from the inventory record, can not be edited
    inventory_id = db.Column(db.Integer, db.ForeignKey('inventory.id', onupdate="CASCADE", ondelete="SET NULL"))  #"inventory" must be lower case
    profit = db.Column(db.Float)
    '''
    def __repr__(self):
        return '<OrderItem %r>' % (self.title)
    '''


class Inventory(SerializableModel):
    __searchable__ = ['asin', 'supplier', 'sellerSKU', 'fnsku', 'condition', 'totalSupplyQuantity', 'inStockSupplyQuantity', 'category', 'orderQuantity']
    #__searchable__ = ['asin', 'supplier', 'sellerSKU', 'fnsku', 'condition', 'totalSupplyQuantity', 'inStockSupplyQuantity', 'category']

    id = db.Column(db.Integer, primary_key = True)
    asin = db.Column(db.String)
    supplier = db.Column(db.String, db.ForeignKey('suppliers.name', onupdate="CASCADE", ondelete="SET NULL"))
    sellerSKU = db.Column(db.String, nullable=False)
    fnsku = db.Column(db.String, nullable=False)
    condition = db.Column(db.String)
    totalSupplyQuantity = db.Column(db.Integer)
    inStockSupplyQuantity = db.Column(db.Integer)
    costEach = db.Column(db.Float)
    category = db.Column(db.String, db.ForeignKey('categories.name', onupdate="CASCADE", ondelete="SET NULL"))
    orderQuantity = db.Column(db.Integer)
    orders = db.relationship('Orderitems', backref='inventoryRecord', lazy='dynamic')

    '''
    def __repr__(self):
        return '<Inventory %r>' % (self.fnsku)
    '''

class Categories(SerializableModel):
    __searchable__ = ['name']

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, unique=True)
    # this backref cant be named the same as "category" because Inventory already has this column
    inventories = db.relationship('Inventory', backref='categoryRecord', lazy='dynamic')

class Suppliers(SerializableModel):
    __searchable__ = ['name']

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String, unique=True)
    inventories = db.relationship('Inventory', backref='supplierRecord', lazy='dynamic')
'''
if enable_search:
    wa.whoosh_index(app, Orderitems)
    wa.whoosh_index(app, Inventory)
    wa.whoosh_index(app, Categories)
    wa.whoosh_index(app, Suppliers)
'''
wa.whoosh_index(app, Orderitems)
wa.whoosh_index(app, Inventory)
wa.whoosh_index(app, Categories)
wa.whoosh_index(app, Suppliers)




