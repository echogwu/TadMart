#!flask/bin/python
from src import app, db

from flask_script import Manager
from flask_migrate import Migrate, MigrateCommand

migrate = Migrate(app, db)
manager = Manager(app)

#"db" is a alias for MigrateCommand, which is a function(might has args, but functions like init/migrate/update doesn't have args)
manager.add_command('db', MigrateCommand)

class INVENTORY(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    asin = db.Column(db.String)
    supplier = db.Column(db.String)
    sellerSKU = db.Column(db.String, nullable=False)
    fnsku = db.Column(db.String, nullable=False)
    condition = db.Column(db.String)
    totalSupplyQuantity = db.Column(db.Integer)
    inStockSupplyQuantity = db.Column(db.Integer)
    costEach = db.Column(db.Float)
    orders = db.relationship('ORDERITEMS', backref='inventoryRecord', lazy='dynamic')

    def __repr__(self):
        return '<InventoryRecord %r>' % (self.id + " " + self.asin)


class ORDERITEMS(db.Model):
    id = db.Column(db.Integer, primary_key = True)
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
    #inventory_id = db.Column(db.Integer, db.ForeignKey('INVENTORY.id'))

    def __repr__(self):
        return '<OrderItem %r>' % (self.id + self.asin + " " + self.title)

if __name__ == "__main__":
    manager.run()


'''
The migration is used to modify db schema without drop the original tables and data stored in them.
$ python migrate.py db init      //generate a directory ./migration/
$ python migrate.py db migrate   //It will compare the present db schema and the schema in this file
$ python migrate.py db upgrade   //upgrade to the schema in this file
'''
