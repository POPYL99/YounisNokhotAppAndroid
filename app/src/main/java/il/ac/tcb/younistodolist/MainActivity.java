package il.ac.tcb.younistodolist;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> items;  //שם אחד ו החדר
    private ArrayList<String> documentIds;
    private ArrayList<Boolean> attendanceStatus;
    private CustomAdapter customAdapter;
    private ListView listView;
    private Button button;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.listView);
        button = findViewById(R.id.button);

        items = new ArrayList<>();
        documentIds = new ArrayList<>();
        attendanceStatus = new ArrayList<>();

        //  4 هنا نمرر documentIds  משתנים
        customAdapter = new CustomAdapter(this, items, attendanceStatus, documentIds);
        listView.setAdapter(customAdapter);

        button.setOnClickListener(v -> {
            EditText inputName = findViewById(R.id.editTextText);
            EditText inputClassNumber = findViewById(R.id.editTextClassNumber);

            // שטח ה שם ו החדר
            String NameStudent = inputName.getText().toString();
            String classNumber = inputClassNumber.getText().toString();

            if (!NameStudent.isEmpty() && !classNumber.isEmpty()) {
                // إنشاء خريطة جديدة للطالب وإضافة الاسم ورقم الصف
                Map<String, Object> student = new HashMap<>();
                student.put("name", NameStudent);
                student.put("classNumber", classNumber);
                student.put("attendance", false); // إضافة حالة الحضور الافتراضية

                // إضافة الطالب إلى قاعدة البيانات
                firestore.collection("students").add(student).addOnSuccessListener(documentReference -> {
                    //להוסיפ את השם ו החדר של תלמיד
                    items.add(NameStudent + " - " + classNumber); // عرض الاسم ورقم الصف معًا
                    documentIds.add(documentReference.getId());
                    attendanceStatus.add(false); // إضافة حالة الحضور الافتراضية
                    customAdapter.notifyDataSetChanged();
                    inputName.setText("");
                    inputClassNumber.setText("");
                    Toast.makeText(getApplicationContext(), "Student Addedd", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Student Not Addedd", Toast.LENGTH_LONG).show();
                });
            } else {
                Toast.makeText(getApplicationContext(), "Pleas Enter the neme and ClassRoom", Toast.LENGTH_LONG).show();
            }
        });
    }
    //To delet the sTudent By Id
    public void deleteStudent(int index) {
        //get the id from the Documentid from the index
        String documentId = documentIds.get(index);
        // تحديد مجموعة الطلاب في Firestore وحذف الوثيقة التي تطابق الـ documentId
        firestore.collection("students").document(documentId).delete() // محاولة حذف الوثيقة
                .addOnSuccessListener(aVoid -> {
                    items.remove(index);
                    documentIds.remove(index);
                    attendanceStatus.remove(index);
                    customAdapter.notifyDataSetChanged(); // تحديث واجهه التغييرات
                    Toast.makeText(getApplicationContext(), "Student removed", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to remove student", Toast.LENGTH_LONG).show();
                });
    }

}