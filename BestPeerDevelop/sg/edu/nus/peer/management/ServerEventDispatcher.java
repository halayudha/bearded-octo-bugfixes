/*
 * @(#) ServerEventDispatcher.java 1.0 2006-2-3
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.management;

import sg.edu.nus.accesscontrol.normalpeer.CreateUserFromBoostrapListener;
import sg.edu.nus.bestpeer.joinprocessing.JoinProcessingListener;
import sg.edu.nus.bestpeer.joinprocessing.TableStatSearchListener;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.event.AccessControlRoleACK;
import sg.edu.nus.peer.event.AttachListener;
import sg.edu.nus.peer.event.CacheDbIndexListener;
import sg.edu.nus.peer.event.ERPDeleteColumnIndexListener;
import sg.edu.nus.peer.event.ERPDeleteDataIndexListener;
import sg.edu.nus.peer.event.ERPDeleteTableIndexListener;
import sg.edu.nus.peer.event.ERPInsertColumnIndexListener;
import sg.edu.nus.peer.event.ERPInsertDataIndexListener;
import sg.edu.nus.peer.event.ERPInsertTableIndexListener;
import sg.edu.nus.peer.event.ERPUpdateColumnIndexListener;
import sg.edu.nus.peer.event.ERPUpdateDataIndexListener;
import sg.edu.nus.peer.event.ForceOutListener;
import sg.edu.nus.peer.event.LeaveListener;
import sg.edu.nus.peer.event.LoginAckListener;
import sg.edu.nus.peer.event.QueryPeerListener;
import sg.edu.nus.peer.event.QueryPeerResultListener;
import sg.edu.nus.peer.event.RolemanListener;
import sg.edu.nus.peer.event.SPIndexDeleteBundleListener;
import sg.edu.nus.peer.event.SPIndexDeleteListener;
import sg.edu.nus.peer.event.SPIndexInsertBundleListener;
import sg.edu.nus.peer.event.SPIndexInsertListener;
import sg.edu.nus.peer.event.SPIndexSearchListener;
import sg.edu.nus.peer.event.SPIndexUpdateBundleListener;
import sg.edu.nus.peer.event.SPIndexUpdateListener;
import sg.edu.nus.peer.event.SPJoinAcceptListener;
import sg.edu.nus.peer.event.SPJoinForceForwardListener;
import sg.edu.nus.peer.event.SPJoinForceListener;
import sg.edu.nus.peer.event.SPJoinListener;
import sg.edu.nus.peer.event.SPJoinSplitDataListener;
import sg.edu.nus.peer.event.SPLBFindLightlyNodeListener;
import sg.edu.nus.peer.event.SPLBGetLoadInfoListener;
import sg.edu.nus.peer.event.SPLBGetLoadInfoReplyListener;
import sg.edu.nus.peer.event.SPLBGetLoadInfoResendListener;
import sg.edu.nus.peer.event.SPLBNoRotationNodeListener;
import sg.edu.nus.peer.event.SPLBRotateUpdateAdjacentListener;
import sg.edu.nus.peer.event.SPLBRotateUpdateAdjacentReplyListener;
import sg.edu.nus.peer.event.SPLBRotateUpdateChildListener;
import sg.edu.nus.peer.event.SPLBRotateUpdateChildReplyListener;
import sg.edu.nus.peer.event.SPLBRotateUpdateParentListener;
import sg.edu.nus.peer.event.SPLBRotateUpdateParentReplyListener;
import sg.edu.nus.peer.event.SPLBRotateUpdateRTListener;
import sg.edu.nus.peer.event.SPLBRotateUpdateRTReplyListener;
import sg.edu.nus.peer.event.SPLBRotationPullListener;
import sg.edu.nus.peer.event.SPLBSplitDataListener;
import sg.edu.nus.peer.event.SPLBSplitDataResendListener;
import sg.edu.nus.peer.event.SPLBStablePositionListener;
import sg.edu.nus.peer.event.SPLIAdjacentListener;
import sg.edu.nus.peer.event.SPLIAdjacentReplyListener;
import sg.edu.nus.peer.event.SPLIAdjacentRootListener;
import sg.edu.nus.peer.event.SPLIAdjacentRootReplyListener;
import sg.edu.nus.peer.event.SPLIChildReplyListener;
import sg.edu.nus.peer.event.SPLIRoutingTableListener;
import sg.edu.nus.peer.event.SPLIRoutingTableReplyListener;
import sg.edu.nus.peer.event.SPLIUpdateParentListener;
import sg.edu.nus.peer.event.SPLeaveFindReplaceListener;
import sg.edu.nus.peer.event.SPLeaveFindReplaceReplyListener;
import sg.edu.nus.peer.event.SPLeaveListener;
import sg.edu.nus.peer.event.SPLeaveNotifyListener;
import sg.edu.nus.peer.event.SPLeaveReplacementListener;
import sg.edu.nus.peer.event.SPLeaveUrgentListener;
import sg.edu.nus.peer.event.SPNotifyFailureListener;
import sg.edu.nus.peer.event.SPNotifyImbalanceListener;
import sg.edu.nus.peer.event.SPPassClientListener;
import sg.edu.nus.peer.event.SPUpdateAdjacentLinkListener;
import sg.edu.nus.peer.event.SPUpdateMinMaxValueListener;
import sg.edu.nus.peer.event.SPUpdateRouteTableDirectlyListener;
import sg.edu.nus.peer.event.SPUpdateRouteTableIndirectlyListener;
import sg.edu.nus.peer.event.SPUpdateRouteTableListener;
import sg.edu.nus.peer.event.SPUpdateRouteTableReplyListener;
import sg.edu.nus.peer.event.SPWebSearchListener;
import sg.edu.nus.peer.event.SchemaListener;
import sg.edu.nus.peer.event.TableIndexSearchListener;
import sg.edu.nus.peer.event.TableIndexSearchResultListener;
import sg.edu.nus.peer.event.TableRetrievalListener;
import sg.edu.nus.peer.event.TableTupleListener;
import sg.edu.nus.peer.info.CacheDbIndex;

/**
 * Implement a concrete event manager used for monitoring
 * all incoming socket connections related to the bootstrap server.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-3
 */

public class ServerEventDispatcher extends AbstractEventDispatcher {

	public ServerEventDispatcher(AbstractMainFrame gui) {
		super(gui);
	}

	@Override
	public void registerActionListeners() {
		
		//VHTam
		this.addActionListener(new CacheDbIndexListener(gui));
		
		/* used for monitoring client peer's events */
		this.addActionListener(new AttachListener(gui));
		this.addActionListener(new LeaveListener(gui));
		this.addActionListener(new ForceOutListener(gui));
		

		/* join and insert data objects */
		this.addActionListener(new SPIndexInsertBundleListener(gui));
		this.addActionListener(new SPIndexInsertListener(gui));

		this.addActionListener(new SPJoinAcceptListener(gui));
		this.addActionListener(new SPJoinListener(gui));
		this.addActionListener(new SPJoinForceListener(gui));
		this.addActionListener(new SPJoinForceForwardListener(gui));
		this.addActionListener(new SPJoinSplitDataListener(gui));

		/* load balance */
		this.addActionListener(new SPNotifyImbalanceListener(gui));
		this.addActionListener(new SPLBFindLightlyNodeListener(gui));
		this.addActionListener(new SPLBGetLoadInfoListener(gui));
		this.addActionListener(new SPLBGetLoadInfoReplyListener(gui));
		this.addActionListener(new SPLBGetLoadInfoResendListener(gui));
		this.addActionListener(new SPLBNoRotationNodeListener(gui));
		this.addActionListener(new SPLBRotateUpdateAdjacentListener(gui));
		this.addActionListener(new SPLBRotateUpdateAdjacentReplyListener(gui));
		this.addActionListener(new SPLBRotateUpdateChildListener(gui));
		this.addActionListener(new SPLBRotateUpdateChildReplyListener(gui));
		this.addActionListener(new SPLBRotateUpdateParentListener(gui));
		this.addActionListener(new SPLBRotateUpdateParentReplyListener(gui));
		this.addActionListener(new SPLBRotateUpdateRTListener(gui));
		this.addActionListener(new SPLBRotateUpdateRTReplyListener(gui));
		this.addActionListener(new SPLBSplitDataListener(gui));
		this.addActionListener(new SPLBSplitDataResendListener(gui));
		this.addActionListener(new SPLBStablePositionListener(gui));
		this.addActionListener(new SPLBRotationPullListener(gui));

		/* leave the network */
		this.addActionListener(new SPLeaveListener(gui));
		this.addActionListener(new SPLeaveUrgentListener(gui));
		this.addActionListener(new SPLeaveNotifyListener(gui));
		this.addActionListener(new SPLeaveFindReplaceListener(gui));
		this.addActionListener(new SPLeaveFindReplaceReplyListener(gui));
		this.addActionListener(new SPLeaveReplacementListener(gui));
		this.addActionListener(new SPPassClientListener(gui));

		/* update links with other super peers */
		this.addActionListener(new SPUpdateAdjacentLinkListener(gui));
		this.addActionListener(new SPUpdateMinMaxValueListener(gui));
		this.addActionListener(new SPUpdateRouteTableListener(gui));
		this.addActionListener(new SPUpdateRouteTableDirectlyListener(gui));
		this.addActionListener(new SPUpdateRouteTableIndirectlyListener(gui));
		this.addActionListener(new SPUpdateRouteTableReplyListener(gui));

		/* delete and search data objects*/
		this.addActionListener(new SPIndexDeleteBundleListener(gui));
		this.addActionListener(new SPIndexDeleteListener(gui));
		this.addActionListener(new SPIndexUpdateBundleListener(gui));
		this.addActionListener(new SPIndexUpdateListener(gui));
		/*
		 * end of modification
		 * $Id 2007-2-2 14:23 author: xulinhao$
		 */

		this.addActionListener(new SPIndexSearchListener(gui));
		/* end of modification
		 * $Id 2007-2-1 11:10 author: xulinhao$
		 */

		/* failure recovery*/
		this.addActionListener(new SPLIAdjacentListener(gui));
		this.addActionListener(new SPLIAdjacentRootListener(gui));
		this.addActionListener(new SPLIAdjacentReplyListener(gui));
		this.addActionListener(new SPLIAdjacentRootReplyListener(gui));
		this.addActionListener(new SPLIRoutingTableListener(gui));
		this.addActionListener(new SPLIRoutingTableReplyListener(gui));
		this.addActionListener(new SPLIChildReplyListener(gui));
		this.addActionListener(new SPNotifyFailureListener(gui));
		this.addActionListener(new SPLIUpdateParentListener(gui));

		// for downloading request
		// this.addActionListener(new DownloadListener(gui));

		// VHTam: add following code to the next //end VHTam
		// for user create from bootstrap

		this.addActionListener(new CreateUserFromBoostrapListener(gui));

		// for join query processing
		this.addActionListener(new TableStatSearchListener(gui));
		this.addActionListener(new JoinProcessingListener(gui));
		// end VHTam

		// David added this
		this.addActionListener(new TableIndexSearchListener(gui));
		this.addActionListener(new TableIndexSearchResultListener(gui));
		this.addActionListener(new QueryPeerListener(gui));
		this.addActionListener(new QueryPeerResultListener(gui));
		// end David added

		this.addActionListener(new ERPInsertTableIndexListener(gui));
		this.addActionListener(new ERPInsertColumnIndexListener(gui));
		this.addActionListener(new ERPInsertDataIndexListener(gui));
		this.addActionListener(new ERPDeleteTableIndexListener(gui));
		this.addActionListener(new ERPDeleteColumnIndexListener(gui));
		this.addActionListener(new ERPDeleteDataIndexListener(gui));
		this.addActionListener(new ERPUpdateColumnIndexListener(gui));
		this.addActionListener(new ERPUpdateDataIndexListener(gui));

		// Wusai added this
		this.addActionListener(new TableRetrievalListener(gui));
		this.addActionListener(new TableTupleListener(gui));
		this.addActionListener(new SPWebSearchListener(gui));
		// end Wusai add

		// chensu added this
		this.addActionListener(new LoginAckListener(gui));
		this.addActionListener(new SchemaListener(gui));
		this.addActionListener(new RolemanListener(gui));

		this.addActionListener(new AccessControlRoleACK(gui));
		// end chensu add
	}
}