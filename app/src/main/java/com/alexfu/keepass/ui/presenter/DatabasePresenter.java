package com.alexfu.keepass.ui.presenter;

import android.content.ContentResolver;
import android.net.Uri;

import com.alexfu.keepass.ui.view.DatabaseView;
import com.keepassdroid.database.DatabaseManager;
import com.keepassdroid.database.KDB;
import com.keepassdroid.database.exception.InvalidDBException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DatabasePresenter extends ViewPresenter {
  
  private DatabaseView view;
  private Uri kdbUri;
  
  public DatabasePresenter(DatabaseView view) {
    super(view);
    this.view = view;
  }
  
  public void openDatabase(Uri kdbUri) {
    this.kdbUri = kdbUri;
    view.showAuthenticationView();
  }
  
  public void authenticate(String password) {
    openDatabase(password)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<KDB>() {
          private KDB result;
          
          @Override
          public void onCompleted() {
            if (result != null) {
              view.onAuthenticated(result);
            }
          }

          @Override
          public void onError(Throwable e) {
            e.printStackTrace();
            // TODO: Show error view            
          }

          @Override
          public void onNext(KDB kdb) {
            result = kdb;
          }
        });
  }
  
  private Observable<KDB> openDatabase(String password) {
    String scheme = kdbUri.getScheme();
    
    if (scheme.equals("content")) {
      return openWithContentResolver(password);
    }
    
    return null;
  }
  
  private Observable<KDB> openWithContentResolver(final String password) {
    return Observable.create(new Observable.OnSubscribe<KDB>() {
      @Override
      public void call(Subscriber<? super KDB> subscriber) {
        ContentResolver resolver = view.getAppContext().getContentResolver();
        try {
          InputStream is = resolver.openInputStream(kdbUri);
          DatabaseManager dbm = new DatabaseManager();
          dbm.LoadData(is, password, "");
          
          subscriber.onNext(dbm.kdb);
          subscriber.onCompleted();
        } catch (FileNotFoundException e) {
          subscriber.onError(e);
        } catch (InvalidDBException e) {
          subscriber.onError(e);
        } catch (IOException e) {
          subscriber.onError(e);
        }
      }
    });
  }

}
