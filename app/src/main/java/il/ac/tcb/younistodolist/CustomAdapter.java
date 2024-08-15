package il.ac.tcb.younistodolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context c;
    private ArrayList<String> students;
    private ArrayList<Boolean> attendanceStatus;
    private ArrayList<String> documentIds; // لإضافة وثيقة firebase ID
    private FirebaseFirestore firestore; // للوصول إلى firestore

    public CustomAdapter(Context context, ArrayList<String> students, ArrayList<Boolean> attendanceStatus, ArrayList<String> documentIds) {
        this.c = context;
        this.students = students;
        this.attendanceStatus = attendanceStatus;
        this.documentIds = documentIds;
        this.firestore = FirebaseFirestore.getInstance(); // تهيئة Firestore
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        // Get references to the views in the layout
        TextView studentName = convertView.findViewById(R.id.studentName);
        ImageView attendanceIcon = convertView.findViewById(R.id.attendanceIcon);
        ImageView deleteIcon = convertView.findViewById(R.id.deleteIcon);

        // Set the student name
        studentName.setText(students.get(position));

        // Set the attendance icon based on the attendance status
        if (attendanceStatus.get(position)) {
            attendanceIcon.setImageResource(R.drawable.ic_check); // Icon for present (check mark)
        } else {
            attendanceIcon.setImageResource(R.drawable.ic_delete); // Icon for absent (cross mark)
        }

        // Set up the delete icon click listener
        deleteIcon.setOnClickListener(v -> {
            // Perform the delete action (e.g., remove from database)
            if (c instanceof MainActivity) {
                ((MainActivity) c).deleteStudent(position);
            }
        });

        // hear im shanging the X to Y
        attendanceIcon.setOnClickListener(v -> {
            boolean currentStatus = attendanceStatus.get(position);
            boolean newStatus = !currentStatus; // Toggle the status
            attendanceStatus.set(position, newStatus);

            // Update Firebase to the stats
            String documentId = documentIds.get(position);
            firestore.collection("students").document(documentId)
                    .update("attendance", newStatus)
                    .addOnSuccessListener(aVoid -> {
                        notifyDataSetChanged(); // Refresh the list view
                        Toast.makeText(c, "Attendance updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(c, "Failed to update attendance", Toast.LENGTH_SHORT).show();
                    });
        });

        return convertView;
    }
}
