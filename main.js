var nodemailer = require('nodemailer');

var mailer = {
    from: 'info@blinqui.com',
    options: {
        service: 'gmail',
        host: 'smtp.gmail.com',
        port: 465,
        secure: true, // use SSL
        auth: {
            user: 'info@blinqui.com',
            pass: 'cymtdyomeejoyeus'
        }
    }
};

var smtpTransport = nodemailer.createTransport(mailer.options);

// SEND EMAIL TO USER
Parse.Cloud.define('sendEmailToUser', async (request) => {
	var emailId = request.params.emailId;
	var sub = request.params.subject;
	var body = request.params.body;


	console.log('====>>'+emailId);
	console.log('====>>'+sub);
	console.log('====>>'+body);


	var mailOptions = {
                    to: emailId,
                    bcc: 'stubshq.appdeveloper@gmail.com',
                    from: mailer.from,
                    subject: sub,
                    html: body,
                };
                console.log('===mailOptions=>>'+mailOptions);
                smtpTransport.sendMail(mailOptions, function (err) {
                    console.log('EMAIL SENDER ERR ===> ', err);
                });
});




// RESEND VERIFICATION EMAIL
Parse.Cloud.define('resendVerificationEmail', async (request) => {
	var originalEmail = request.params.email;
	const User = Parse.Object.extend("User");

	const query = new Parse.Query(User);
	query.equalTo("email", originalEmail);
	var userObject = await query.first({useMasterKey: true});

	if(userObject !=null)
	{
		userObject.set("email", "tmp_email_prefix_"+originalEmail);
		await userObject.save(null, {useMasterKey: true}).catch(error => {throw 'Something went wrong';});
		userObject.set("email", originalEmail);
		await userObject.save(null, {useMasterKey: true}).catch(error => {throw 'Something went wrong';});

    	return 'success';
	}
});

// UPDATE WRONG EMAIL
Parse.Cloud.define('updateWrongEmail', async (request) => {
	var wrongEmail = request.params.emailWrong;
	var newEmail = request.params.emailNew;
	const User = Parse.Object.extend("User");

	const query = new Parse.Query(User);
	query.equalTo("email", wrongEmail);
	var userObject = await query.first({useMasterKey: true});

	if(userObject !=null)
	{
		userObject.set("email", newEmail);
		userObject.set("username", newEmail);
		await userObject.save(null, {useMasterKey: true}).catch(error => {throw 'Something went wrong';});

    	return 'success';
	}
});

// GET SERVER CURRENT TIME
Parse.Cloud.define("getServerDate", async (request) => {
    var dateToday = new Date();
    return dateToday.toISOString();
});

Parse.Cloud.define("getServerDateSpecific", function(request, response) {
    var dateToday = new Date();
	response.success(dateToday.toISOString().substring(0, 10));
});


// SEND PUSH NOTIFICATION TO USER
Parse.Cloud.define("pushToUser", (request) => {
	var userId = request.params.userId;
	var alertTitle = request.params.alertTitle;
	var alertText = request.params.alertText;

	var pushQuery = new Parse.Query(Parse.Installation);
  	pushQuery.equalTo("userId", userId);
  	pushQuery.equalTo("isAdmin", false);

    return Parse.Push.send({
        // channels: ["News"],
        where: pushQuery,
        data: {
            title: alertTitle,
            alert: alertText,
        }
    }, { useMasterKey: true });
});


// SEND PUSH NOTIFICATION TO ADMIN
Parse.Cloud.define("pushToAdmin", (request) => {
	var alertTitle = request.params.alertTitle;
	var alertText = request.params.alertText;

	var pushQuery = new Parse.Query(Parse.Installation);
  	pushQuery.equalTo("isAdmin", true);

    return Parse.Push.send({
        where: pushQuery,
        data: {
            title: alertTitle,
            alert: alertText,
        }
    }, { useMasterKey: true });
});

// UPDATE USER DATA FROM ADMIN APP
Parse.Cloud.define('updateUserData', async (request) => {
	var userId = request.params.userId;
	var category = request.params.category;
	var status = request.params.status;
	var reason = request.params.reason;
	var comment = request.params.comment;
	var isMediaApproved = request.params.isMediaApproved;
	var isMediaPending = request.params.isMediaPending;

	const User = Parse.Object.extend("User");

	const query = new Parse.Query(User);
	query.equalTo("objectId", userId);
	var userObject = await query.first({useMasterKey: true});

	if(userObject !=null)
	{
		userObject.set("groupCategory", category);
		userObject.set("accountStatus", status);
		userObject.set("rejectReason", reason);
		userObject.set("rejectComment", comment);
		userObject.set("isMediaApproved", isMediaApproved);
		userObject.set("isMediaPending", isMediaPending);
		await userObject.save(null, {useMasterKey: true}).catch(error => {throw 'Something went wrong';});
    	return 'success';
	}
});

// UPDATE USER PIC FROM ADMIN APP
Parse.Cloud.define('updateUserPic', async (request) => {
	var userId = request.params.userId;
	var pic = request.params.profilePic;

	const User = Parse.Object.extend("User");

	const query = new Parse.Query(User);
	query.equalTo("objectId", userId);
	var userObject = await query.first({useMasterKey: true});

	if(userObject !=null)
	{
		userObject.set("profilePic", pic);
		await userObject.save(null, {useMasterKey: true}).catch(error => {throw 'Something went wrong';});
    	return 'success';
	}
});

// UPDATE USER PHOTO STATUS FROM ADMIN APP
Parse.Cloud.define('resetUserPhotosStatus', async (request) => {
	var userId = request.params.userId;

	const User = Parse.Object.extend("User");

	const query = new Parse.Query(User);
	query.equalTo("objectId", userId);
	var userObject = await query.first({useMasterKey: true});

	if(userObject !=null)
	{
		userObject.set("isPhotosSubmitted", false);
		await userObject.save(null, {useMasterKey: true}).catch(error => {throw 'Something went wrong';});
    	return 'success';
	}
});

// UPDATE USER VIDEO STATUS FROM ADMIN APP
Parse.Cloud.define('resetUserVideoStatus', async (request) => {
	var userId = request.params.userId;

	const User = Parse.Object.extend("User");

	const query = new Parse.Query(User);
	query.equalTo("objectId", userId);
	var userObject = await query.first({useMasterKey: true});

	if(userObject !=null)
	{
		userObject.set("isVideoSubmitted", false);
		await userObject.save(null, {useMasterKey: true}).catch(error => {throw 'Something went wrong';});
    	return 'success';
	}
});


