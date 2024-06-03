Problem statement -

Food recommendation system.

eed to create a system where employees can give ratings, feedback.
There'll be 3 roles -
	• Chef
	• Admin
	• Employee or user.
System will help the employees to give their recommendation.
It will also help the chef to know employee's interests.

Features needed to be implemented -
Features needed to be implemented -

	• Authentication and Authorization.
		○ Login feature.
		○ Access to only those API's based on role assigned to them.
		○ Employees can login using their employee ID and name.
	
	• Menu Management
		○ Admin can add, update, and delete menu items with prices and availability.

	• Food recommendation
		○ Role - Chef
			§ Chef will roll out X items for breakfast, lunch, dinner.
			§ One day before - (n-1)
			§ X items will be shown to employees.
			§ Employees will choose what they want by EOD.
			§ Nth day cafe owner will prepare food according to the resoponse.
			§ Each food has multiple attributes -
				□ Consumer comments.
				□ Consumer ratings.
				□ Date of provided feedback.
			§ When chef sends food items to employees it must go through with recommendation engine.
				□ This engine will find ratings of the food and show to the user.
				□ It will also find the comments and show to the user.
		○ Role - Employee
			§ Employee can give feedback on any food item from menu including breakfast, lunch and dinner.
		○ Role - Reporting
			§ Monthly report will be generated for food items for chef.
		○ Notifications
			§ Notifications are sent through socket.
				□ For next day food recommendation.
				□ For any new food item.
				□ Availability status of any food item.

