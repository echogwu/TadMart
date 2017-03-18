from flask import render_template, flash, redirect, session, url_for, request, g, jsonify, json, abort
from flask.json import dumps
from src import app, db
from sqlalchemy import create_engine, MetaData
from .models import Inventory, Orderitems
from config import ENTRIES_PER_PAGE, SQLALCHEMY_DATABASE_URI

@app.errorhandler(404)
def not_found_error(error):
    return jsonify({'error': 404})
    #return dumps({'error': 404})

@app.errorhandler(500)
def internal_error(error):
    db.session.rollback()
    return jsonify({'error': 500})
    #return dumps({'error': 500})

@app.route("/tables")
def index():
    engine = create_engine(SQLALCHEMY_DATABASE_URI)
    metadata = MetaData.reflect(engine)
    tables = metadata.tables.keys()
    return jsonify({'tables': tables})
    #return dumps(tables)

@app.route("/inventory", methods=['GET'])
@app.route('/inventory/pages/<int:page>', methods=['GET'])
def listInventorySupplies(page = 1):
    '''
    returs: supplies are of the type of Inventory.
    '''
    #the function paginate(page=None, per_page=None, error_out=True) returns an Pagination object.
    suppliers = Inventory.query.order_by(Inventory.id.desc()).paginate(page, ENTRIES_PER_PAGE, False).items
    if not suppliers:
        abort(404)
    return jsonify(suppliers = Inventory.serialize_list(suppliers))
    #return dumps(suppliers)

@app.route("/inventory/<int:id>", methods=['GET'])
def getSupply(id):
    supply = Inventory.query.filter_by(id=id).first()
    if not supply:
        abort(404)
    return jsonify(supply=supply.serialize())
    #return dumps(supply)

@app.route('/inventory/<int:id>/edit', methods=['Get', 'PUT'])
#@login_required
def editInventory(id):
    supply = Inventory.query.filter_by(id=id).first()
    if not supply:
        abort(404)
    #args_dict = request.form.to_dict(flat=True)
    #http://stackoverflow.com/questions/10434599/how-to-get-data-recieved-in-flask-request decide to use form/args/data..
    args_dict = json.loads(request.data)
    print("args:", request.args)
    print("form:", request.form)
    print("values:", request.values)
    print("data:", request.data)
    if supply and args_dict:
        for k, v in args_dict.items():
            setattr(supply, k, v)
        db.session.commit()
        flash("Your changes have been saved.")
    if supply == None:
        flash("This inventory with id %d doesn't exist" % id)
    if args_dict == None:
        flash("Did you change anything?")
    redirect(url_for('getSupply', id=id)) 
    return jsonify(supply = supply.serialize())
    #return dumps(supply)

@app.route('/inventory/last')
def lastInventory():
    inventory = Inventory.query.all()
    if not inventory:
        abort(404)
    return jsonify(inventory = inventory[-1].serialize())

@app.route('/inventory/add', methods=['GET','POST'])
def addInventory():
    supply = Inventory()
    #args_dict = request.args.to_dict(flat=True)
    args_dict = json.loads(request.data)
    if args_dict:
        for k, v in args_dict.items():
            setattr(supply, k, v)
        db.session.add(supply)
        db.session.commit()
        flash("Added a new supply entry")
    redirect(url_for('getSupply', id=Inventory.query.all()[-1].id))
    return jsonify(supply = supply.serialize())
    #return dumps(supply)

@app.route("/", methods=['GET'])
@app.route("/index", methods=['GET'])
@app.route("/orders", methods=['GET'])
@app.route('/orders/pages/<int:page>', methods=['GET'])
def listOrders(page = 1):
    '''
    returs: orders are of the type of Orderitems.
    '''
    #the function paginate(page=None, per_page=None, error_out=True) returns an Pagination object.
    orders = Orderitems.query.order_by(Orderitems.lastUpdateDate.desc())
    if not orders:
        abort(404)
    orders = orders.paginate(page, ENTRIES_PER_PAGE, False).items
    return jsonify(orders = Orderitems.serialize_list(orders))
    #return dumps(orders)

@app.route("/orders/<int:id>", methods=['GET'])
def getOrder(id):
    order = Orderitems.query.filter_by(id=id).first()
    if not order:
        abort(404)
    return jsonify(order = order.serialize())
    #return dumps(order)

@app.route('/orders/<int:id>/edit', methods=['Get', 'PUT'])
#@login_required
def editOrder(id):
    order = Orderitems.query.filter_by(id=id).first()
    if not order:
        abort(404)
    #args_dict = request.args.to_dict(flat=True)
    args_dict = json.loads(request.data)
    if order and args_dict:
        for k, v in args_dict.items():
            setattr(order, k, v)
    db.session.commit()
    flash('Your changes have been saved.')
    redirect(url_for('getOrder', id=id))
    return jsonify(order = order.serialize())
    #return dumps(order)

@app.route('/orders/last')
def lastOrder():
    orders = Orderitems.query.all()
    if not orders:
        abort(404)
    return jsonify(inventory = orders[-1].serialize())

@app.route('/orders/add', methods=['GET','POST'])
def addOrder():
    order = Orderitems()
    #args_dict = request.args.to_dict(flat=True)
    args_dict = json.loads(request.data)
    if args_dict:
        for k, v in args_dict.items():
            setattr(order, k, v)
        db.session.add(order)
        db.session.commit()
        flash("Added a new order entry")
    redirect(url_for('getOrder', id=Orderitems.query.all()[-1].id))
    return jsonify(order = order.serialize())
    #return dumps(order)
