package fr.acinq.chainswap.app

import fr.acinq.eclair._
import scala.concurrent.duration._
import fr.acinq.bitcoin.{ByteVector32, Satoshi}
import scodec.bits.ByteVector
import akka.actor.ActorRef


case class PeerAndConnection(peer: ActorRef, connection: ActorRef)

case class AccountAndAddress(accountId: String, btcAddress: String)

case class ChainDepositReceived(accountId: String, amount: Satoshi, txid: String, depth: Long)

case class BTCDeposit(id: Long, btcAddress: String, outIndex: Long, txid: String, amount: Long, depth: Long, stamp: Long) {
  def toPendingDeposit: PendingDeposit = PendingDeposit(btcAddress, ByteVector32(ByteVector fromValidHex txid), Satoshi(amount), stamp)
}

// Protocol messages

sealed trait ChainSwapMessage

sealed trait SwapIn

case object SwapInRequest extends SwapIn with ChainSwapMessage

case class SwapInResponse(btcAddress: String, minChainDeposit: Satoshi) extends SwapIn with ChainSwapMessage

case class SwapInPaymentRequest(paymentRequest: String) extends SwapIn with ChainSwapMessage

case class SwapInPaymentDenied(paymentRequest: String, reason: String) extends SwapIn with ChainSwapMessage

case class PendingDeposit(btcAddress: String, txid: ByteVector32, amount: Satoshi, stamp: Long)

case class SwapInState(balance: MilliSatoshi, inFlight: MilliSatoshi, pendingChainDeposits: List[PendingDeposit] = Nil) extends SwapIn with ChainSwapMessage

sealed trait SwapOut

case object SwapOutRequest extends SwapOut with ChainSwapMessage

case class BlockTargetAndFee(blockTarget: Int, fee: Satoshi)

case class KeyedBlockTargetAndFee(feerates: List[BlockTargetAndFee], feerateKey: ByteVector32)

case class SwapOutFeerates(feerates: KeyedBlockTargetAndFee, providerCanHandle: Satoshi, minWithdrawable: Satoshi) extends SwapOut with ChainSwapMessage

case class SwapOutTransactionRequest(amount: Satoshi, btcAddress: String, blockTarget: Int, feerateKey: ByteVector32) extends SwapOut with ChainSwapMessage

case class SwapOutTransactionResponse(paymentRequest: String, amount: Satoshi, fee: Satoshi) extends SwapOut with ChainSwapMessage

case class SwapOutTransactionDenied(btcAddress: String, reason: String) extends SwapOut with ChainSwapMessage