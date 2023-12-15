# Final-Project
 Final Project for CS410 Fall2023 <br>
 This project includes different classes as listed in below 
<h2> UserApp </h2>
- UserApp is our main class that our code mainly loops on. <br> 
 The user can choose specified options under rules by typing a number.
- Ever part of the selection has a return button if they desire to get back to <br>
main loop.

<h2> ClassManagment </h2>
<h3> 1: Create a class </h3>

- This part takes:
  - Class no, section no, term, and description as a info 
- then creates a class in the database Class table.

<h3> 2: List classes </h3>

- Prints the classes listed in Class table.

  <h3> 3: Activate a class </h3>

- This part activates the desired class with:
  - Course Number, term, and section number informations.
- Not all of these information has to be given, user can only put course number <br>
and it would be a valid input. <br>
- After finding the class from input it saves the class as a "current class" in the file.

<h3> 4: Show Current Active Class </h3>

- Prints the information about current active class

<h2> CategoryandAssignmentManagment </h2>

<h3> 1: Show Category </h3>
  
- Prints the categories from Category Table.

<h3> 2: Add a Category </h3>
   
- This part takes:
  - category name, weight.
- and adds a new category to the Category table with these values

<h3> 3: Show Assignments </h3>
   
- Prints the assignments from Category Table.

<h3> 4: Add a Assignment </h3>
   
- This part takes:
  - name, description, point and category.
- After getting all input, it checks if the category exists, if not <br>
it will asked again until user gives a correct category and <br>
creates a new assignment.

<h2> StudentManagment </h2>


<h3> 1: Add a Student </h3>

- Add a new student with:
  - student name, username. Student ID is automatically created
- Check if the student exists, if so, inform the user and enroll the student <br>
current class, if not create a new student in the Student table and enroll as well.


<h3> 2: Enroll Existing Student </h3>

- Take username from the user, if the username is correct, enroll that student <br>
to the current class. If the student already enrolled, inform the user.

<h3> 3: Show all Students </h3>

- Prints the students from the Student table.

<h3> 4: Show a Specific Student </h3>

- Prints the student with given information.

<h3> 5: Grade a Student </h3>

- Grade a student with:
  - student username, assignment name.
- Check if the student exists and get the max possible points for that assignment.
- If student is not already graded, create a new value in StudentGrades.
- If student is already graded, inform the user, update the grade for that assignment.



<h2> GradeReport </h2>

<h3> 1: Student Grades </h3>

- Show a students grade with:
  - student username.
- This will return every grade related to current class and it is ordered by category.


<h3> 2: Show Gradebook </h3>

- Show all grades associated with current class:

<h2> GradeCalculation </h2>
- U
