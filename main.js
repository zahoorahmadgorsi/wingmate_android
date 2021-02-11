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
    // return dateToday.toDateString();
	// return dateToday.toISOString().substring(0, 10);
});


Parse.Cloud.define("getServerDateX", function(request, response) {
    	var dateToday = new Date();
	response.success(dateToday.toISOString().substring(0, 10));
    	//response.success(dateToday.toDateString());
});