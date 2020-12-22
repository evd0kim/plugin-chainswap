package fr.acinq.chainswap.app

import fr.acinq.eclair._
import fr.acinq.chainswap.app.wire.Codecs._
import org.scalatest.funsuite.AnyFunSuite


class WireSpec extends AnyFunSuite {
  test("Correctly process inner messages") {
    assert(decode(toUnknownMessage(SwapInRequest)).require === SwapInRequest)

    val pd1 = PendingDeposit(btcAddress = "1RustyRX2oai4EYYDpQGWvEL62BBGqN9T", txid = randomBytes32, amount = 200000.sat, System.currentTimeMillis / 1000L)
    val pd2 = PendingDeposit(btcAddress = "1RustyRX2oai4EYYDpQGWvEL62BBGqN9T", txid = randomBytes32, amount = 400000.sat, System.currentTimeMillis / 1000L)
    val inner1 = SwapInState(balance = 1000.msat, inFlight = 100000L.msat, List(pd1, pd2))
    assert(decode(toUnknownMessage(inner1)).require === inner1)

    val btaf1 = BlockTargetAndFee(blockTarget = 6, 200.sat)
    val btaf2 = BlockTargetAndFee(blockTarget = 12, 100.sat)
    val inner2 = SwapOutFeerates(KeyedBlockTargetAndFee(List(btaf1, btaf2), randomBytes32), 10000000000L.sat, 50000L.sat)
    assert(decode(toUnknownMessage(inner2)).require === inner2)

    val sor = SwapOutTransactionResponse("00" * 512, 100000000000L.sat, 90000.sat)
    assert(decode(toUnknownMessage(sor)).require === sor)

    val swd = SwapInPaymentDenied(paymentRequest = "00" * 512, reason = "00" * 64)
    assert(decode(toUnknownMessage(swd)).require === swd)

    val sod = SwapOutTransactionDenied(btcAddress = "00" * 32, reason = "00" * 512)
    assert(decode(toUnknownMessage(sod)).require === sod)
  }
}
