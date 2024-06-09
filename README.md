Problem statement -

Food recommendation system.

We need to create a system where employees can give ratings and feedback.
There will be 3 roles -
	• Chef
	• Admin
	• Employee or user.
The system will help the employees to give their recommendations.
It will also help the chef to know the employee's interests.

Features needed to be implemented -

1. Authentication and Authorization.
	1. Login feature.
	2. Access to only those APIs based on the role assigned to them.
	3. Employees can log in using their employee ID and name.

2. Menu Management
	1. Admin can add, update, and delete menu items with prices and availability.

3. Food recommendation
   
1. Role - Chef
	1. Chef will roll out X items for breakfast, lunch, and dinner.
	2. He will roll the menu one day before - (n-1)
	3. X items will be shown to employees.
	4. Employees will choose what they want by EOD by voting.
	5. On the nth day the cafe owner will prepare food according to the response or voting result.
	6. Each food has multiple attributes -
		1. Consumer comments.
		2. Consumer ratings.
		3. Date of provided feedback.
	7. When a chef sends food items to employees they must go through with the recommendation engine.
		1. This engine will find and show food ratings to the user.
		2. It will also find and show the comments to the user.
2. Role - Employee
	1. An employee can give feedback on any food item from the menu including breakfast, lunch, and dinner.
3. Role - Reporting
	1. A monthly report will be generated for food items for the chef.
4. Notifications
	1. Notifications are sent through socket.
		1. For next-day food recommendations.
		2. For any new food item.
		3. Availability status of any food item.
