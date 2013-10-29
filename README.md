concrete
========

A (very) simple ORM for Android.

Let's say you have a table called Foo with the string myText, the integer myNumber and the date myDate. All you have to do to get basic CRUD operations on this table is to create this object:

    package com.briskbee.concrete.example.record;

    import java.util.Date;
    import android.content.Context;
    import com.briskbee.concrete.Record;

    public class Foo extends Record<Foo> {
        public String myText;
        public int myNumber;
        public Date myDate;

        public Foo(Context context) {
            super(context);
        }
    }

Please note all variables of the class are public. This is necessary for the framework to be able to recognize them as properties of the class (and columns of the table). So now we have an object let's use it. Let's say we are inside an activity and want to create a row in the table foo.

    Foo foo = new Foo(this);
    foo.myText = "some text";
    foo.myNumber = 3;
    foo.myDate = new Date();
    foo.save();

That is basically all there is to it. No need for writing tedious CRUD code anymore. It is as DRY as it gets. Retrieving, updating and deleting records are just as easy:

    // Load foo
    Foo foo = new Foo(this);
    foo.load(1);

    // Update foo
    foo.myText = "some other text";
    foo.update();

    // Delete foo
    foo.delete();

You still need to have your own implementation of SQLiteOpenHelper which creates the tables for you.

Please note that relations are not supported.
