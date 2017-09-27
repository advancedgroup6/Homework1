var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
mongoose.connect('mongodb://swati4jha:Kite+123@ds161713.mlab.com:61713/mongodb4swati');
var jwt = require('jsonwebtoken');
var Schema = mongoose.Schema;


// schema defines how mongoose will write the data to the database.

var regionSchema = new Schema({
    majorminor: {type:String,required:true},
    category:{type:String,unique:true,required:true}
}, {collection:'region'});

var userDataSchema = new Schema({
    name: {type:String,required:true},
    emailID:{type:String,unique:true,required:true},
    address:{type:String,required:true},
    phoneNo:{type:String, required:true},
    password:{type:String, required:true}
}, {collection:'users'});

var msgSchema = new Schema({
    sender: {type:String},
    receiver:{type:String},
    date: {type:Date},
    isRead:{type:Boolean},
    isLocked:{type:Boolean},
    msg: {type:String},
    region:{type:String},
    majorminor:{type:String}
}, {collection:'messages'});

var Region = mongoose.model('Region', regionSchema);
var UserData = mongoose.model('UserData', userDataSchema);
var MsgData = mongoose.model('MsgData', msgSchema);

router.post('/signup',function(req, resp, next){
    console.log('came to insert');

    var item = {
        name : req.body.name,
        emailID:req.body.emailID,
        address:req.body.address,
        phoneNo:req.body.phoneNo,
        password:req.body.password
    };
    // item needs to have the same structure as defined in the schema of the UserData
    var data = new UserData(item);
    data.save(function(err, product, numAffected){
        if(err){
            if(err.code == 11000){
                console.log(err.code);
                resp.json({"status":"failure", "message":"user already exists"})
            }else{
                resp.json({"status":"500", "message":"internal server error"});
            }
            // console.log("error : " + err.description);
        }else{
            // console.log(product);
            var user = product;
            var token = jwt.sign(user,user.emailID, { expiresIn: 14400 });
            resp.json({"status":"200","message":"sign-up success", "token":token,"userDetails":product});
        }
    });
});

    router.post('/login', function(req, resp, next){
    var emailID = req.body.emailID;// hard code the item to update the ID.
    var password = req.body.password;
    UserData.find({emailID:emailID}, function(err, doc){
        if(err){
            console.error('error, no entry found.');
        }
        console.log(doc);
        var user = doc[0];

        if(user!=undefined && password == user.password){
            var token = jwt.sign(user,user.emailID, { expiresIn: 14400 });
            resp.json({"status":"200", "message":"login success","token":token,"userDetails":user});
        }else{
            resp.json({"status":"500", "message":"couldn't login in."});
        }


    });
    // UserData.findById(emailID, );
});

function checkToken(token,emailID) {

    //const token = req.headers['x-access-token'];
    console.log(token);
    if (token) {
        console.log(emailID);
        try {
            var decoded = jwt.verify(token, emailID);
            console.log( decoded._doc.emailID);
            return decoded._doc.emailID === emailID;

        } catch(err) {
            console.log("error");
            return false;
        }

    } else {

        return false;
    }
}

router.post('/sendMsg',function(req, resp, next){
    console.log('came to insert');

    var majorminor = req.body.majorminor;
    var token = req.body.token;
    var emailID = req.body.sender;
    if(checkToken(token,emailID)){
        Region.find({majorminor:majorminor}, function(err, doc) {
            if (err) {
                console.error('Error finding proper beacon.');
            } else {
                var category = doc[0].category;
                console.log(category);
                var item = {
                    sender : req.body.sender,
                    receiver:req.body.receiver,
                    date:new Date(),
                    isRead:false,
                    isLocked:true,
                    msg:req.body.msg,
                    region:category,
                    majorminor:majorminor
                };
                // item needs to have the same structure as defined in the schema of the UserData
                var data = new MsgData(item);
                data.save(function(err, product, numAffected){
                    if(err){
                        resp.json({"status":"500", "message":"internal server error"});
                    }else{
                        // console.log(product);
                        var msg = product;
                        resp.json({"status":"200","message":"Message sent successfully!!", "mgsDetails":msg});
                    }
                });
            }
        });
    } else{
        resp.json({"status":"500", "message":"Login error"});
    }

});


router.get('/deleteMsg/:id/:token/:emailID', function(req, resp, next){
    var id = req.params.id;
    var token = req.params.token;
    var emailID = req.params.emailID;
    if(checkToken(token,emailID)){
        MsgData.deleteOne({_id:id}, function(err, doc) {
            if (err) {
                console.error('Error deleting message.');
            } else {
                resp.json({"status":"200","message":"Message successfully deleted!"});
            }
        });
    } else{
        resp.json({"status":"500", "message":"Login error"});
    }

});

    router.get('/updateMsgRead/:id/:token/:emailID', function(req, resp, next){
    var id = req.params.id;
    var token = req.params.token;
    var emailID = req.params.emailID;
    if(checkToken(token,emailID)){
        MsgData.updateOne(
            {_id:id},
            {
                $set: { isRead: true }
            }, function(err, messg){
                if (err) {
                    console.error('Error finding proper beacon.');
                    resp.json({"status":"200","message":"Error updating message!!"});
                } else {
                    resp.json({"status":"200","message":"Message updated successfully!!"});
                }
            });
    } else{
        resp.json({"status":"500", "message":"Login error"});
    }

});

router.get('/updateMsgLock/:id', function(req, resp, next){
    var id = req.params.id;
    MsgData.updateOne(
        {_id:id},
        {
            $set: { isLocked: false }
        }, function(err, messg){
            if (err) {
                console.error('Error finding proper beacon.');
                resp.json({"status":"200","message":"Error updating message!!"});
            } else {
                resp.json({"status":"200","message":"Message updated successfully!!"});
            }
        });
});

router.get('/updateMsgs/:receiver/:region/:token', function(req, resp, next){
    var receiver = req.params.receiver;
    var region = req.params.region;
    var token = req.params.token;
    var emailID = receiver;
    console.log("Inside update");
    if(checkToken(token,emailID)){
        console.log("Inside update1");
        console.log(receiver);
        console.log(region);
        MsgData.updateMany(
            {receiver:receiver,majorminor:region},
            {
                $set: { isLocked: false }
            }, function(err, messg){
                if (err) {
                    console.error('Error finding proper beacon.');
                    resp.json({"status":"200","message":"Error updating message!!"});
                } else {
                    MsgData.find({receiver:receiver}).sort({"date": -1}).exec(function(err, messg) {
                        if (err) {
                            console.error('Error updating message!!');
                        } else {
                            console.log(messg);
                            resp.json({"status":"200","message":"Message fetched successfully!!", "msgList":messg});
                        }
                    });

                }
            });
    } else{
        resp.json({"status":"500", "message":"Login error"});
    }

});

router.get('/getMessages/:receiver/:token', function(req, resp, next){
    var receiverID = req.params.receiver;
    var token = req.params.token;
    var emailID = receiverID;
    if(checkToken(token,emailID)){
        MsgData.find({receiver:receiverID}).sort({"date": -1}).exec(function(err, messg) {
            if (err) {
                console.error('Error finding proper beacon.');
            } else {
                console.log(messg);
                resp.json({"status":"200","message":"Message fetched successfully!!", "msgList":messg});
            }
        });
    } else{
        resp.json({"status":"500", "message":"Login error"});
    }

});

router.get('/getAllMsgs', function(req, resp, next){
    MsgData.find().sort( {"date": -1}).exec(function(err, messg) {
        if (err) {
            console.error('Error finding proper beacon.');
        } else {
            console.log(messg);
            resp.json({"status":"200","message":"Message sent successfully!!", "msgList":messg});
        }
    });
});


router.get('/getAllUsers/:token/:emailID', function(req, resp, next){
    var token = req.params.token;
    var emailID = req.params.emailID;
    if(checkToken(token,emailID)){
        UserData.find({} ,function(err, messg) {
            if (err) {
                console.error('Error finding proper beacon.');
            } else {
                console.log(messg);
                for(i=0;i<messg.length;i++){
                    messg[i].password="";
                }
                resp.json({"status":"200","message":"Users fetched successfully!!", "usersList":messg});
            }
        });
    } else{
        resp.json({"status":"500", "message":"Login error"});
    }

});


router.get('/getAllRegions/:token/:emailID', function(req, resp, next){
    var token = req.params.token;
    var emailID = req.params.emailID;
    if(checkToken(token,emailID)){
        Region.find({} ,function(err, messg) {
            if (err) {
                console.error('Error finding proper beacon.');
            } else {
                console.log(messg);
                resp.json({"status":"200","message":"Message sent successfully!!", "regionList":messg});
            }
        });
    } else{
        resp.json({"status":"500", "message":"Login error"});
    }

});


/* GET home page. */
router.get('/', function(req, res, next) {
    res.render('index', { title: 'Express' });
});



module.exports = router;
