package ps.com.mobileusersecuritynew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ps.com.model.UserDao;

public class ActivationScreen extends Activity implements DatePickerDialog.OnDateSetListener {


    private EditText edtFirstName, edtEmail, edtProductValue, edtCellNumber, edtActivationKey, edtInvoiceNumber, edtUniqueId, edtInvoiceDate;
    private Button btnActivate;
    private SessionManager sessionManager;

    /*variabels*/
    private String mIMInumber;
    private String mSimSerialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_screen);
        sessionManager = new SessionManager(ActivationScreen.this);
        Log.d("ActivationScreen", "sessionManager.isLogin()=" + sessionManager.isLogin());
        if (sessionManager.isLogin()) {
            if (sessionManager.isSettingSet()) {
                Intent intent = new Intent(ActivationScreen.this, DashboardScreen.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ActivationScreen.this, SettingActivity.class);
                startActivity(intent);
            }
            finish();
        } else {
            setLayout();
            setData();
        }


    }


    private void setData() {
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getSubscriberId();
        mIMInumber = tMgr.getDeviceId();
        mSimSerialNumber = tMgr.getSimSerialNumber();

        Log.d("setData", "mPhoneNumber=" + mPhoneNumber);
        Log.d("setData", "mIMInumber=" + mIMInumber);
        Log.d("setData", "mSimSerialNumber=" + mSimSerialNumber);


    }


    private void setLayout() {

        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        // edtLastName = (EditText) findViewById(R.id.edt_last_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtProductValue = (EditText) findViewById(R.id.edt_product_value);
        edtCellNumber = (EditText) findViewById(R.id.edt_cell_phone);
        edtActivationKey = (EditText) findViewById(R.id.edt_activation);
        edtInvoiceNumber = (EditText) findViewById(R.id.edt_invoice_number);
        edtUniqueId = (EditText) findViewById(R.id.edt_unique_id);
        edtInvoiceDate = (EditText) findViewById(R.id.edt_invocie_date);
        btnActivate = (Button) findViewById(R.id.btn_login);
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtFirstName.getText().toString().trim().length() == 0) {
                    Toast.makeText(ActivationScreen.this, "Enter first name.", Toast.LENGTH_SHORT).show();
                } else if (edtEmail.getText().toString().trim().length() == 0) {
                    Toast.makeText(ActivationScreen.this, "Enter email.", Toast.LENGTH_SHORT).show();
                } else if (edtProductValue.getText().toString().trim().length() == 0) {
                    Toast.makeText(ActivationScreen.this, "Enter product value.", Toast.LENGTH_SHORT).show();
                } else if (edtCellNumber.getText().toString().trim().length() == 0) {
                    Toast.makeText(ActivationScreen.this, "Enter cell phone number.", Toast.LENGTH_SHORT).show();
                } else if (edtActivationKey.getText().toString().trim().length() == 0) {
                    Toast.makeText(ActivationScreen.this, "Enter activation key.", Toast.LENGTH_SHORT).show();
                } else if (edtInvoiceNumber.getText().toString().trim().length() == 0) {
                    Toast.makeText(ActivationScreen.this, "Enter invoice number.", Toast.LENGTH_SHORT).show();
                } else if (edtUniqueId.getText().toString().trim().length() == 0) {
                    Toast.makeText(ActivationScreen.this, "Enter unique id.", Toast.LENGTH_SHORT).show();
                } else if (edtInvoiceDate.getText().toString().trim().length() == 0) {
                    Toast.makeText(ActivationScreen.this, "Select invoice date.", Toast.LENGTH_SHORT).show();
                } else {
                    if (WebServiceConstants.isOnline(ActivationScreen.this)) {
                        new ActivateAsynchTask(edtActivationKey.getText().toString(), edtFirstName.getText().toString(), edtEmail.getText().toString(), edtProductValue.getText().toString(), edtCellNumber.getText().toString(), edtInvoiceNumber.getText().toString(), edtUniqueId.getText().toString(), edtInvoiceDate.getText().toString()).execute(WebServiceConstants.ACTIVATION);
                    } else {
                        Toast.makeText(ActivationScreen.this, "Please check internet connection.",
                                Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        edtInvoiceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show date picker dialog.
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ActivationScreen.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String month = String.format("%02d", ++monthOfYear);
        String date = String.format("%02d", dayOfMonth);

        edtInvoiceDate.setText(year + "-" + month + "-" + date);

    }


    class ActivateAsynchTask extends AsyncTask<String, Void, JSONObject> {

        private ProgressDialog pDialog;
        private String serial;
        private String name;
        private String email;
        private String productValue;
        private String cellNumber;
        private String invoiceNumber;
        private String uniqueId;
        private String invoiceDate;


        public ActivateAsynchTask(String serial, String name, String email, String productValue, String cellNumber, String invoiceNumber, String uniqueId, String invoiceDate) {
            this.serial = serial;
            this.name = name;
            this.email = email;
            this.productValue = productValue;
            this.cellNumber = cellNumber;
            this.invoiceNumber = invoiceNumber;
            this.uniqueId = uniqueId;
            this.invoiceDate = invoiceDate;


        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivationScreen.this);
            pDialog.setMessage("Please Wait....");
            pDialog.setIndeterminate(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected JSONObject doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            params.add(new BasicNameValuePair("email", WebServiceConstants.EMAIL));
            params.add(new BasicNameValuePair("password", WebServiceConstants.PASSWORD));
            params.add(new BasicNameValuePair("serial", serial));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("user_email", email));
            params.add(new BasicNameValuePair("product_value", productValue));
            params.add(new BasicNameValuePair("cellphone", cellNumber));
            params.add(new BasicNameValuePair("invoice_date", invoiceDate));
            params.add(new BasicNameValuePair("invoice_number", invoiceNumber));
            params.add(new BasicNameValuePair("unique_id", uniqueId));
            params.add(new BasicNameValuePair("imi_array[0][0]", mIMInumber));
            params.add(new BasicNameValuePair("sim_array[0][0]", mSimSerialNumber));


            JSONObject json = new JSONParser().makeHttpRequest2(args[0], "POST",
                    params);
            Log.d("login params", "params=" + params.toString());
            Log.d("login response", "" + json);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            pDialog.dismiss();
            try {

                JSONObject jsonObjectResult = jsonObject.getJSONObject("result");
                String enabeld = jsonObjectResult.getString("enabled");
                if (enabeld.equals("1")) {

                    String antivirus_url = jsonObjectResult.getString("antivirus_url");
                    String expiry_date = jsonObjectResult.getString("expiry_date");
                    expiry_date = expiry_date + " 23:59";
                    expiry_date = Utill.convertDateFormat("yyyy-MM-dd HH:mm", "dd MMM yyyy hh:mm aa", expiry_date);

                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
                    String activeDate = simpleDateFormat.format(date);

                    // String antivirus_url = "https://play.google.com/store/apps/details?id=com.cleanmaster.mguard";
                    SessionManager sessionManager = new SessionManager(ActivationScreen.this);
                    UserDao userDao = sessionManager.getUserDetail();
                    userDao.setUserFirstName(name);
                    userDao.setUserEmail(email);
                    userDao.setUserActivationKey(serial);
                    userDao.setSimSerialNumber(mSimSerialNumber);
                    userDao.setActivationDateTime(activeDate);
                    userDao.setExpireDateTime(expiry_date);
                    try {
                        userDao.setAntivirusPackageName(antivirus_url.split("=")[1]);
                    } catch (Exception e) {
                        userDao.setAntivirusPackageName("com.cleanmaster.mguard");
                    }

                    sessionManager.setUserDetail(userDao);

//                    /*start service*/
//                    Intent intentService = new Intent(ActivationScreen.this, BackUpService.class);
//                    startService(intentService);
                    /*start dasboard actvity*/
                    Intent intent = new Intent(ActivationScreen.this, SettingActivity.class);
                    startActivity(intent);
                    Toast.makeText(ActivationScreen.this, "Thanks your account is active now. Please take a moment and configure your app.", Toast.LENGTH_SHORT).show();
                    finish();
                    sessionManager.startCheckExpairationOfLicenceAlarmManager();

                } else if (enabeld.equals("0")) {
                    Toast.makeText(
                            ActivationScreen.this,
                            "Account not activated.",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(
                        ActivationScreen.this,
                        "Invalid activation key.",
                        Toast.LENGTH_SHORT).show();
            }


        }
    }


}
