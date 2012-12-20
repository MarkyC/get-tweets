package name.marcocirillo;

import java.util.List;

import javax.swing.SwingWorker;

import twitter4j.Status;

public class ModelThread extends Thread /*SwingWorker<Float, Integer>*/ {
	
	private Model model;
	
	private float oldProgress;
	
	/**
	 * Runs a Model object in a separate thread
	 * @param model - the Model to run in a separate thread
	 */
	public ModelThread(Model model) {
		super();
		
		this.model = model;		// set this model to the passed model
		this.oldProgress = 0;
	}
	
	@Override
	public void run() {
		for (int i = 1; i < ((float) (model.getMaxStatuses()/model.getStatusPerPage())); i++) {
	     	// Get the next 100 statuses from the specified user 
	        List<Status> statuses = null;
	        
	        statuses = model.getStatusPageList(i);	
			
	        if (statuses == null) {
	        	DebugCrash.printDebugInfo("Failed to get status list", 
	        		new NullPointerException());
	        }
	        		 
	        model.writeStatusesToFile(statuses);
	        
	        //this.updateProgress((float) model.getStatusPerPage() / model.getMaxStatuses());
	        this.oldProgress = (float) model.getStatusPerPage() / model.getMaxStatuses();
	    }
	    
	  
		model.finishAndShow();
	}

	/*
	@Override
	protected Float doInBackground() throws Exception {
		for (int i = 1; i < ((float) (model.getMaxStatuses()/model.getStatusPerPage())); i++) {
	     	// Get the next 100 statuses from the specified user 
	        List<Status> statuses = null;
	        
	        statuses = model.getStatusPageList(i);	
			
	        if (statuses == null) {
	        	DebugCrash.printDebugInfo("Failed to get status list", 
	        		new NullPointerException());
	        }
	        		 
	        model.writeStatusesToFile(statuses);
	        
	        //this.updateProgress((float) model.getStatusPerPage() / model.getMaxStatuses());
	        this.oldProgress = (float) model.getStatusPerPage() / model.getMaxStatuses();
	    }
	    
	  
		model.finishAndShow();
		return this.oldProgress;
	}

	private void updateProgress(float f) {
		// TODO Auto-generated method stub
		
	}*/


}