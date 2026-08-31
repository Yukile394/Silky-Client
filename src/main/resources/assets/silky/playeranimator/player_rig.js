// Default Silky player-rig graph bootstrap.
(() => {
  const R=globalThis.SilkyPlayerRig;
  if (!R || typeof R.PlayerRigMotionGraph!=='function') throw new Error('Silky player rig modules were not loaded');
  globalThis.SilkyPlayerAnimations=R;
  const graph=new R.PlayerRigMotionGraph(playerRig);
  globalThis.__silky_default_player_rig_graph=graph;
  playerRig.onPose(context=>graph.apply(context));
})();
