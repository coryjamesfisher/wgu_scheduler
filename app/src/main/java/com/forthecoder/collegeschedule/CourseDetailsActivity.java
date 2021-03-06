package com.forthecoder.collegeschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.forthecoder.collegeschedule.entity.AssessmentRepository;
import com.forthecoder.collegeschedule.entity.Course;
import com.forthecoder.collegeschedule.entity.CourseRepository;
import com.forthecoder.collegeschedule.exception.ApplicationException;

import java.text.DateFormat;

/**
 * Requirement A6E: Detailed View
 * This activity shows the details of a particular course including
 * all of the course information title, start date, end date, and status.
 */
public class CourseDetailsActivity extends BaseActivity {

    private Course course;
    private Long termId;

    public CourseDetailsActivity() {
        super();
        contentLayout = R.layout.activity_course_details;
        actionLayout = R.layout.activity_course_details_actions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final CourseRepository cr = new CourseRepository(getDatabase());
        try {
            course = cr.findOneByRowid(getIntent().getLongExtra("rowid", 0L));
        } catch (ApplicationException e) {
        }
        termId = course.getTermId();

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        ((TextView)findViewById(R.id.courseTitleValue)).setText(course.getTitle());
        ((TextView)findViewById(R.id.courseStartValue)).setText(dateFormat.format(course.getStartDate()));
        ((TextView)findViewById(R.id.courseEndValue)).setText(dateFormat.format(course.getAnticipatedEndDate()));
        ((TextView)findViewById(R.id.courseStatusValue)).setText(course.getStatus());


        /*
         * Requirement A6D: Optional Notes
         * This activity shows the notes for the course inline with the rest of the details.
         */
        ((TextView)findViewById(R.id.courseNotesValue)).setText(course.getNotes());

        FloatingActionButton fab = findViewById(R.id.edit_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToTarget(view, course.getRowid());
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.share_button);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder message = new StringBuilder();
                message.append("Course: ")
                        .append(course.getTitle()).append("\n")
                        .append("Notes:\n")
                        .append(course.getNotes());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });

        FloatingActionButton fab3 = findViewById(R.id.remove_button);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    /*
                     * Requirement A6C: Course Information
                     * This handler will delete all information for a course.
                     */
                    final AssessmentRepository ar = new AssessmentRepository(getDatabase());
                    ar.deleteAllForCourse(course.getRowid());
                    cr.delete(course);
                    navigateToTarget(CoursesActivity.class, null, termId);
                } catch (ApplicationException ignored) {
                }
            }
        });

        Button upLevelButton = findViewById(R.id.upLevelButton);
        upLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToTarget(CoursesActivity.class, null, termId);
            }
        });
    }

    public void navigateToMentorsList(View view) {
        this.navigateToTarget(view, null, course.getRowid());
    }

    public void navigateToAssessmentsList(View view) {
        this.navigateToTarget(view, null, course.getRowid());
    }
}
