package com.jianglibo.vaadin.dashboard.uicomponent.upload;

import org.springframework.context.MessageSource;

import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

@SuppressWarnings("serial")
public class ImmediateUploader extends HorizontalLayout {

	 private Label status = new Label("");

	private Upload upload;

	private Button cancelBtn;
	
	public ImmediateUploader(MessageSource messageSource,ReceiverWithEventListener receiver) {
		setSpacing(true);
		this.upload = new Upload("", receiver);
		this.upload.addStyleName("uploadwrapper");

		addComponent(status);
		addComponent(upload);
		
		setComponentAlignment(status, Alignment.MIDDLE_RIGHT);

		// Make uploading start immediately when file is selected
		upload.setImmediate(true);
		upload.setButtonCaption(messageSource.getMessage("component.upload.selectfile", null, UI.getCurrent().getLocale()));

		cancelBtn = new Button(messageSource.getMessage("component.upload.cancel", null, UI.getCurrent().getLocale()));
		cancelBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				upload.interruptUpload();
				new Notification(messageSource.getMessage("component.upload.interrupt", null, UI.getCurrent().getLocale()), "", Notification.Type.WARNING_MESSAGE)
				.show(Page.getCurrent());

			}
		});
		addComponent(cancelBtn);
		cancelBtn.setVisible(false);

		/**
		 * =========== Add needed listener for the upload component: start,
		 * progress, finish, success, fail ===========
		 */

		upload.addStartedListener(new StartedListener() {
			@Override
			public void uploadStarted(StartedEvent event) {
				upload.setVisible(false);
				cancelBtn.setVisible(true);
				status.setValue(event.getFilename());
			}
		});

		upload.addProgressListener(new ProgressListener() {
			@Override
			public void updateProgress(long readBytes, long contentLength) {
				double d = (double)readBytes/contentLength;
				String per = Math.round(d * 100) + "%";
				cancelBtn.setCaption(messageSource.getMessage("component.upload.cancel", null, UI.getCurrent().getLocale()) + " " + per);
			}
		});

		upload.addSucceededListener(new SucceededListener() {
			@Override
			public void uploadSucceeded(SucceededEvent event) {
				receiver.uploadSucceeded(event);
				new Notification(messageSource.getMessage("component.upload.success", new String[]{event.getFilename()}, UI.getCurrent().getLocale()), "", Notification.Type.TRAY_NOTIFICATION)
				.show(Page.getCurrent());
			}
		});
		

		upload.addFailedListener(new FailedListener() {
			@Override
			public void uploadFailed(FailedEvent event) {
				receiver.uploadFailed(event);
				new Notification(messageSource.getMessage("component.upload.fail", new String[]{event.getFilename()}, UI.getCurrent().getLocale()), "", Notification.Type.ERROR_MESSAGE)
				.show(Page.getCurrent());
			}
		});

		upload.addFinishedListener(new FinishedListener() {
			@Override
			public void uploadFinished(FinishedEvent event) {
				status.setValue("");
				cancelBtn.setVisible(false);
				upload.setVisible(true);
				receiver.uploadFinished(event);
			}
		});
	}
}
