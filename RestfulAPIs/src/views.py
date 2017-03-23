from flask import render_template, flash, redirect, session, url_for, request, g, jsonify, json, abort
from src import app, db
from sqlalchemy import create_engine, MetaData
from .models import Inventory, Orderitems, Categories, Suppliers
from config import SQLALCHEMY_DATABASE_URI, ENTRIES_PER_PAGE, MAX_SEARCH_RESULTS

def serialize_paginated_data(paginated_data):
    return {
        'page': paginated_data.page,
        'per': paginated_data.per_page,
        'total': paginated_data.total,
        'pages': paginated_data.pages,
        'data': [item.serialize() for item in paginated_data.items],
    }

@app.errorhandler(404)
def not_found_error(error):
    return jsonify({'status': 404, 'error': 'Not found'}), 404

@app.errorhandler(500)
def internal_error(error):
    db.session.rollback()
    return jsonify({'status': 500, 'error': 'Internal server error'}), 500

class BadRequest(Exception):
    status_code = 400

    def __init__(self, message='Bad request', status_code=None):
        Exception.__init__(self)
        if status_code is not None:
            self.status_code = status_code
        self.message = message

    def to_response(self):
        response = {'error': self.message}
        response.status_code = self.status_code
        return response

@app.errorhandler(BadRequest)
def bad_request(error):
    return error.to_response()

@app.route('/api/v1.0/tables')
def index():
    tables = []
    for t in db.metadata.tables.items():
        tables.append(t[0])
    print(tables)
    return jsonify({'tables': tables})

@app.route('/api/v1.0/inventory', methods=['GET'])
def listInventorySupplies():
    '''
    returs: supplies are of the type of Inventory.
    '''
    #the function paginate(page=None, per_page=None, error_out=True) returns an Pagination object.
    #curl -i "http://127.0.0.1:5000/inventory?page=1&per=4"
    #the url must be inside the double quote.
    #http://stackoverflow.com/questions/30586601/flask-only-sees-first-parameter-from-multiple-parameters-sent-with-curl
    page = int(request.args.get('page', 1))
    per = int(request.args.get('per', ENTRIES_PER_PAGE))
    suppliers = Inventory.query.order_by(Inventory.id.desc()).paginate(page, per, False)
    return jsonify(serialize_paginated_data(suppliers))

@app.route("/api/v1.0/inventory/<int:id>", methods=['GET'])
def getSupply(id):
    supply = Inventory.query.filter_by(id=id).first()
    if not supply:
        abort(404)
    return jsonify(data=supply.serialize())

@app.route('/api/v1.0/inventory/<int:id>', methods=['PUT'])
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
    for k, v in args_dict.items():
        setattr(supply, k, v)
    db.session.commit()
    return jsonify(data=supply.serialize())

@app.route('/api/v1.0/inventory/last')
def lastInventory():
    inventory = Inventory.query.all()
    return jsonify(data=inventory[-1].serialize())

@app.route('/api/v1.0/inventory', methods=['POST'])
def addInventory():
    supply = Inventory()
    #args_dict = request.args.to_dict(flat=True)
    args_dict = json.loads(request.data)
    if not args_dict:
        raise BadRequest
    for k, v in args_dict.items():
        setattr(supply, k, v)
    db.session.add(supply)
    db.session.commit()
    return jsonify(data=supply.serialize()), 201

'''
@app.route('/inventory', methods=['DELETE'])
def deleteInventory():
    args_dict = json.loads(request.data)
    id = args_dict['id']
    supply = Inventory.query.filter_by(id=id).first()
    if not supply:
        abort(404)
    db.session.delete(supply)
    db.session.commit()
    return jsonify(data=supply.serialize())
'''

@app.route('/api/v1.0/orders', methods=['GET'])
def listOrders():
    '''
    returs: orders are of the type of Orderitems.
    '''
    #the function paginate(page=None, per_page=None, error_out=True) returns an Pagination object.
    page = int(request.args.get('page', 1))
    per = int(request.args.get('per', ENTRIES_PER_PAGE))
    orders = Orderitems.query.order_by(Orderitems.lastUpdateDate.desc()).paginate(page, per, False)
    return jsonify(serialize_paginated_data(orders))

@app.route('/api/v1.0/orders/<int:id>', methods=['GET'])
def getOrder(id):
    order = Orderitems.query.filter_by(id=id).first()
    if not order:
        abort(404)
    return jsonify(data=order.serialize())

@app.route('/api/v1.0/orders/<int:id>', methods=['PUT'])
#@login_required
def editOrder(id):
    order = Orderitems.query.filter_by(id=id).first()
    if not order:
        abort(404)
    args_dict = json.loads(request.data)
    for k, v in args_dict.items():
        setattr(order, k, v)
    db.session.commit()
    return jsonify(data=order.serialize())


@app.route('/api/v1.0/orders/last')
def lastOrder():
    orders = Orderitems.query.all()
    return jsonify(data=orders[-1].serialize())


@app.route('/api/v1.0/orders', methods=['POST'])
def addOrder():
    order = Orderitems()
    args_dict = json.loads(request.data)
    for k, v in args_dict.items():
        setattr(order, k, v)
    db.session.add(order)
    db.session.commit()
    return jsonify(data=order.serialize()), 201

'''
@app.route('/orders', methods=['DELETE'])
def deleteOrder():
    args_dict = json.loads(request.data)
    id = args_dict['id']
    order = Orderitems.query.filter_by(id=id).first()
    if not order:
        abort(404)
    db.session.delete(order)
    db.session.commit()
    return jsonify(data=order.serialize())
'''

@app.route('/api/v1.0/orders/search', methods=['GET'])
#@login_required
def search_order():
    query = request.args.get('query', '').encode('ascii','ignore')
    print("query=",query)
    print("type(query)=%s" % type(query))
    #results = Orderitems.query.whoosh_search(query, MAX_SEARCH_RESULTS).all()
    #results = Orderitems.query.whoosh_search(query).all()
    results = Orderitems.query.whoosh_search("TadMart")
    print(results)
    return jsonify(data=Orderitems.serialize_list(results))
    
@app.route('/api/v1.0/inventory/search', methods=['GET'])
#@login_required
def search_inventory():
    query = request.args.get('query', '')
    results = Inventory.query.whoosh_search(query, MAX_SEARCH_RESULTS)
    return jsonify(serialize_paginated_data(results))

@app.route('/api/v1.0/orders', methods=['GET'])
def getOrdersPerInventorySupply():
    inventory_id = int(request.args.get('inventory_id', 0))
    page = int(request.args.get('page', 1))
    per = int(request.args.get('per', ENTRIES_PER_PAGE))
    orders = Orderitems.query.filter_by(inventory_id=inventory_id).order_by(Orderitems.lastUpdateDate.desc()).paginate(page, per, False)
    return jsonify(serialize_paginated_data(orders))

@app.route('/api/v1.0/inventory', methods=['GET'])
def getInventoryRecordPerOrder():
    inventory_id = int(request.args.get('inventory_id', 0))
    return getSupply(inventory_id)

@app.route('/api/v1.0/categories', methods=['GET'])
def listCategories():
    page = int(request.args.get('page', 1))
    per = int(request.args.get('per', ENTRIES_PER_PAGE))
    categories = Categories.query.paginate(page, per, False)
    return jsonify(serialize_paginated_data(categories))

@app.route('/api/v1.0/categories/<int:id>', methods=['GET'])
def getCategory(id):
    category = Categories.query.filter_by(id=id).first()
    if not category:
        abort(404)
    return jsonify(data=category.serialize())

@app.route('/api/v1.0/categories', methods=['POST'])
def addCategory():
    category = Categories()
    for k,v in json.loads(request.data).items():
        setattr(category, k, v)
    db.session.add(category)
    db.session.commit()
    return jsonify(data=category.serialize()), 201


@app.route('/api/v1.0/categories/<int:id>', methods=['PUT'])
#@login_required
def editCategory(id):
    category = Categories.query.filter_by(id=id).first()
    if not category:
        abort(404)
    args_dict = json.loads(request.data)
    for k, v in args_dict.items():
        setattr(category, k, v)
    db.session.commit()
    return jsonify(data=category.serialize())

@app.route('/api/v1.0/categories', methods=['DELETE'])
def deleteCategory():
    args_dict = json.loads(request.data)
    id = args_dict['id']
    category = Categories.query.filter_by(id=id).first()
    if not category:
        abort(404)
    db.session.delete(category)
    db.session.commit()
    return jsonify(data=category.serialize())

@app.route('/api/v1.0/suppliers', methods=['GET'])
def listSuppliers():
    page = int(request.args.get('page', 1))
    per = int(request.args.get('per', ENTRIES_PER_PAGE))
    suppliers = Suppliers.query.paginate(page, per, False)
    return jsonify(serialize_paginated_data(suppliers))

@app.route('/api/v1.0/suppliers/<int:id>', methods=['GET'])
def getSupplier(id):
    supplier = Suppliers.query.filter_by(id=id).first()
    if not supplier:
        abort(404)
    return jsonify(data=supplier.serialize())

@app.route('/api/v1.0/suppliers', methods=['POST'])
def addSupplier():
    supplier = Suppliers()
    for k,v in json.loads(request.data).items():
        setattr(supplier, k, v)
    db.session.add(supplier)
    db.session.commit()
    return jsonify(data=supplier.serialize()), 201


@app.route('/api/v1.0/suppliers/<int:id>', methods=['PUT'])
#@login_required
def editSupplier(id):
    supplier = Suppliers.query.filter_by(id=id).first()
    if not supplier:
        abort(404)
    args_dict = json.loads(request.data)
    for k, v in args_dict.items():
        setattr(supplier, k, v)
    db.session.commit()
    return jsonify(data=supplier.serialize())

@app.route('/api/v1.0/suppliers', methods=['DELETE'])
def deleteSupply():
    args_dict = json.loads(request.data)
    id = args_dict['id']
    supply = Suppliers.query.filter_by(id=id).first()
    if not supply:
        abort(404)
    db.session.delete(supply)
    db.session.commit()
    return jsonify(data=supply.serialize())
