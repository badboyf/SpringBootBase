package cn.com.fzk.util;

import org.jooq.Field;
import org.jooq.Record;

import cn.com.ito.user.schema.generated.Tables;
import cn.com.ito.user.schema.generated.tables.pojos.Appointment;

public class RecordToPojo {

  public static Appointment toAppointment(Record record) {
    Field<?>[] field = record.fields(Tables.APPOINTMENT.fields());
    return record.into(field).into(Appointment.class);
  }

}
